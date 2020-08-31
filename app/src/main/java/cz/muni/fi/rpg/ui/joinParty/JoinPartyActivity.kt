package cz.muni.fi.rpg.ui.joinParty

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.json.JsonMapper
import com.google.zxing.Result
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.Invitation
import cz.muni.fi.rpg.ui.AuthenticatedActivity
import cz.muni.fi.rpg.ui.common.toast
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.android.ext.android.inject
import timber.log.Timber

class JoinPartyActivity : AuthenticatedActivity(R.layout.activity_join_party),
    ZXingScannerView.ResultHandler, JoinPartyDialog.Listener {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 1

        fun start(packageContext: Context) {
            packageContext.startActivity(Intent(packageContext, JoinPartyActivity::class.java))
        }
    }

    private val jsonMapper: JsonMapper by inject()

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

        toast("Camera permission is required for QR code scanning", Toast.LENGTH_LONG)
        finish()
    }

    override fun onResume() {
        super.onResume()

        requestCameraPermissionIfNecessary()


        findViewById<ZXingScannerView>(R.id.scanner).let { scanner ->
            scanner.setResultHandler(this)
            scanner.startCamera()
        }
    }

    override fun onPause() {
        super.onPause()

        findViewById<ZXingScannerView>(R.id.scanner).stopCamera()
    }

    override fun onSuccessfulPartyJoin() {
        finish()
    }

    override fun onDialogDismiss() {
        resumeScanning()
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
            Timber.d("Scanned QR code with content ${result.text}")
            val invitation = deserializeInvitation(result.text)

            JoinPartyDialog.newInstance(getUserId(), invitation).show(supportFragmentManager, null)
        } catch (e: JsonProcessingException) {
            val error = "QR code is not valid party invitation"
            Timber.d(e, error)
            toast(error)
            resumeScanning()
        }
    }

    /**
     * @throws JsonProcessingException
     */
    private fun deserializeInvitation(json: String) = jsonMapper.readValue(json, Invitation::class.java)

    private fun resumeScanning() = findViewById<ZXingScannerView>(R.id.scanner).resumeCameraPreview(this)
}