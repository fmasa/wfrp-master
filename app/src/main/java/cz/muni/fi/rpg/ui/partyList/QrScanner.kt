package cz.muni.fi.rpg.ui.partyList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import dagger.android.support.DaggerFragment
import me.dm7.barcodescanner.zxing.ZXingScannerView


class QrScanner : DaggerFragment() {
    private lateinit var scanner: ZXingScannerView

    fun setResultHandler(handler: ZXingScannerView.ResultHandler) {
        scanner.setResultHandler(handler)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        scanner = ZXingScannerView(requireContext())

        return scanner
    }


    override fun onResume() {
        super.onResume()

        scanner.setResultHandler {
            Toast.makeText(requireContext(), "Scanned code ${it.text}", Toast.LENGTH_SHORT).show()
        }

        scanner.startCamera()
    }

    override fun onPause() {
        super.onPause()

        scanner.stopCamera()
    }
}
