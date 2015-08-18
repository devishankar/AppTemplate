package my.project.template.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import my.project.template.R;
import my.project.template.utils.AppConstants;
import my.project.template.utils.Logger;

import java.util.Calendar;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "AboutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tvCopyright = (TextView) findViewById(R.id.tvCopyright);
        ImageView twlink = (ImageView) findViewById(R.id.tw_link);
        ImageView gplink = (ImageView) findViewById(R.id.gp_link);
        ImageView fblink = (ImageView) findViewById(R.id.fb_link);
        TextView tvFeedback = (TextView) findViewById(R.id.tvFeedback);
        ImageView ivAppLogo = (ImageView) findViewById(R.id.ivAppLogo);
        TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
        TextView tvAppName = (TextView) findViewById(R.id.tvAppName);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        int start = 2015;
        Calendar calendar = Calendar.getInstance();
        int current = calendar.get(Calendar.YEAR);

        Logger.d(TAG, "year " + current);
        String copyRight = getString(R.string.copyright);
        if (start == current)
            copyRight += start + " " + AppConstants.COMPANY + ".";
        else
            copyRight += start + " - " + current + " " + AppConstants.COMPANY + ".";

        copyRight += "\r\n" + "All rights reserved.";

        tvCopyright.setText(copyRight);

        twlink.setOnClickListener(this);
        gplink.setOnClickListener(this);
        fblink.setOnClickListener(this);
        imageView.setOnClickListener(this);
        tvFeedback.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fb_link:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.fb_link))));
                break;
            case R.id.gp_link:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.gp_link))));
                break;
            case R.id.tw_link:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.tw_link))));
                break;
            case R.id.imageView:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_domain))));
                break;
            case R.id.tvFeedback:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:howda@whenda.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "How to..");
                //intent.putExtra(Intent.EXTRA_TEXT, "your_text");
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
