package com.YS.speechtotext;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

public class DialogChangeLanguage extends AppCompatDialogFragment
{
    private DialogListner listener;
    private Spinner spinner;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.langugaedialog, null);
        builder.setView(view)
                .setTitle("Set File Name")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String languageName = LanguagesNames.languages[spinner.getSelectedItemPosition()];
                        String code = LanguagesNames.languagesCodes[spinner.getSelectedItemPosition()];
                        listener.changeLanguage(languageName + "-" + code);
                    }
                });
        spinner = view.findViewById(R.id.language);
        spinner.setAdapter(new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_dropdown_item,
                LanguagesNames.languages));
        return builder.create();
    }
    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        try {
            listener = (DialogListner) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement DialogListner");
        }
    }
    public interface DialogListner
    {
        void changeLanguage(String filename);
    }
}
