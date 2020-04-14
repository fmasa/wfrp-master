package cz.muni.fi.rpg.ui

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.zxing.Result
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.invitation.InvalidInvitation
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.muni.fi.rpg.model.domain.party.InvitationToken
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_join_party.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView
import javax.inject.Inject


class JoinPartyActivity : AuthenticatedActivity(R.layout.activity_join_party),
    CoroutineScope by CoroutineScope(Dispatchers.Main),
    ZXingScannerView.ResultHandler {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
    }

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var invitationProcessor: InvitationProcessor

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != CAMERA_PERMISSION_CODE) {
            return
        }

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            return
        }

        Toast.makeText(
            applicationContext,
            "Camera permission is required for QR code scanning",
            Toast.LENGTH_LONG
        ).show()
        finish()
    }

    override fun onResume() {
        super.onResume()

        requestCameraPermissionIfNecessary()

        scanner.setResultHandler(this)
        scanner.startCamera()
    }

    override fun onPause() {
        super.onPause()

        scanner.stopCamera()
    }

    private fun requestCameraPermissionIfNecessary() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    override fun handleResult(result: Result) {
        try {
            acceptInvitation(gson.fromJson(result.text, InvitationToken::class.java))
        } catch (e: InvalidInvitation) {
            val error = "Invitation token is not valid"
            Log.e(localClassName, error, e)
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            scanner.resumeCameraPreview(this)
        } catch (e: JsonSyntaxException) {
            val error = "QR code is not valid party invitation"

            Log.e(localClassName, error, e)
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            scanner.resumeCameraPreview(this)
        }
    }

    private fun acceptInvitation(invitation: InvitationToken) {
        launch {
            try {
                invitationProcessor.accept(getUserId(), invitation)

                Toast.makeText(applicationContext, "Successfully joined party!", Toast.LENGTH_SHORT)
                    .show()

                finish()
            } catch (e: InvalidInvitation) {
                Log.e(localClassName, "Invitation token is not valid", e)
            }
        }
    }
}