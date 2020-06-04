package cz.muni.fi.rpg.ui.joinParty

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.json.JsonMapper
import com.google.zxing.Result
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.muni.fi.rpg.model.domain.party.Invitation
import cz.muni.fi.rpg.ui.AuthenticatedActivity
import kotlinx.android.synthetic.main.activity_join_party.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.android.ext.android.inject

class JoinPartyActivity : AuthenticatedActivity(R.layout.activity_join_party),
    ZXingScannerView.ResultHandler {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
    }

    private val jsonMapper: JsonMapper by inject()
    private val invitationProcessor: InvitationProcessor by inject()

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
            val invitation = deserializeInvitation(result.text)

            Log.d(localClassName, result.text)

            JoinPartyDialog(getUserId(), invitation, invitationProcessor)
                .setOnErrorListener { resumeScanning() }
                .setOnSuccessListener { finish() }
                .setOnDismissListener { resumeScanning() }
                .show(supportFragmentManager, "JoinPartyDialog")
        } catch (e: JsonProcessingException) {
            val error = "QR code is not valid party invitation"

            Log.e(localClassName, error, e)
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            resumeScanning()
        }
    }

    /**
     * @throws JsonProcessingException
     */
    private fun deserializeInvitation(json: String) = jsonMapper.readValue(json, Invitation::class.java)

    private fun resumeScanning() = scanner.resumeCameraPreview(this)
}