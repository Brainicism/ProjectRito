package com.brainicism.prito.projectrito;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.ListPreference;
import android.util.AttributeSet;


public class CustomMultilistPreference extends ListPreference implements OnClickListener{

    private int mClickedDialogEntryIndex;
    private int prevDialogEntryIndex;
    public CustomMultilistPreference(Context context, AttributeSet attrs) {
        super(context, attrs);


    }

    public CustomMultilistPreference(Context context) {
        this(context, null);
    }

    private int getValueIndex() {
        return findIndexOfValue(this.getValue() +"");
    }


    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);

        prevDialogEntryIndex = getValueIndex();       // add this
        mClickedDialogEntryIndex = getValueIndex();
        builder.setSingleChoiceItems(this.getEntries(), mClickedDialogEntryIndex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mClickedDialogEntryIndex = which;

            }
        });

        builder.setPositiveButton("OK", this);
    }

    public  void onClick (DialogInterface dialog, int which)
    {
// when u click Cancel: which = -2;
// when u click     OK: which = -1;

        if(which == -2){
            this.setValue(this.getEntryValues()[prevDialogEntryIndex]+"");
        }
        else {
            this.setValue(this.getEntryValues()[mClickedDialogEntryIndex]+"");
        }
    }
}