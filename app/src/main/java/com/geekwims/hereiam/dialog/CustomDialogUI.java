package com.geekwims.hereiam.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geekwims.hereiam.R;
import com.geekwims.hereiam.activity.AttendanceActivity;



public class CustomDialogUI {
    Dialog dialog;
    RelativeLayout rl;

    @SuppressWarnings("static-access")
    public void dialog(final Context context, String title, String message,
                       final AttendanceActivity.ConnectTask task) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_attend_result);
        dialog.setCancelable(false);

        TextView m = (TextView) dialog.findViewById(R.id.message);
        TextView t = (TextView) dialog.findViewById(R.id.title);

        final Button n = (Button) dialog.findViewById(R.id.btn_result_cancel);
        final Button p = (Button) dialog.findViewById(R.id.next_button);

        rl = (RelativeLayout) dialog.findViewById(R.id.rlmain);

        t.setText(bold(title));
        m.setText(message);

        dialog.show();

        n.setText(bold(context.getString(R.string.confirm)));
        p.setText(bold(context.getString(R.string.retry)));

        n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        if (task != null) {
            p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    dialog.dismiss();
                    task.execute();
                }
            });
        }
    }

    //customize text style bold italic....
    public SpannableString bold(String s) {
        SpannableString spanString = new SpannableString(s);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0,
                spanString.length(), 0);
        spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);

        return spanString;
    }
}