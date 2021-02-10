package com.example.ewallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
//when user clicks on an transaction he can get a bottom sheet that will containt sender and receiver address
public class TransactionBottomSheet extends BottomSheetDialogFragment {
    public TransactionBottomSheet(String from_addr,String to_addr){
        this.fromAddress=from_addr;
        this.toAddress=to_addr;
    }
    TextView from_address;
    TextView to_address;
    String fromAddress,toAddress;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.bottom_modal_transaction,container,false);
        from_address=v.findViewById(R.id.from_address_view);
        to_address=v.findViewById(R.id.to_address_view);
        from_address.setText(fromAddress);
        to_address.setText(toAddress);
        return v;
    }
}
