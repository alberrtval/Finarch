package com.example.finarch.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.finarch.databinding.BottomSheetPremiumBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PremiumBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetPremiumBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetPremiumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAdquirir.setOnClickListener {
            dismiss()
            Toast.makeText(
                requireContext(),
                " Próximamente disponible... :)",
                Toast.LENGTH_LONG
            ).show()
        }

        binding.btnDespues.setOnClickListener {
            dismiss()
        }
    }
}