package com.mkandeel.correctsoc;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.mkandeel.correctsoc.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ClickListener {

    private ActivityMainBinding binding;
    private List<Model> list;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        list = new ArrayList<>();
        adapter = new Adapter(list, this);

        binding.recyclerView.setAdapter(adapter);


        fillRecyclerView();

    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isEnabled = checkUnknowSources();
        if (isEnabled) {
            Dialog dialog = showDialog(R.layout.warning_layout, Gravity.CENTER, false);
            TextView txt = dialog.findViewById(R.id.txt_msg);
            TextView ok = dialog.findViewById(R.id.txt_ok);
            TextView cancel = dialog.findViewById(R.id.txt_cancel);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                    startActivity(intent);
                    dialog.dismiss();
                    dialog.cancel();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    dialog.cancel();
                }
            });

        }
        Log.d("Is Enabled mohamed", "" + isEnabled);


    }

    @SuppressLint("NotifyDataSetChanged")
    private void fillRecyclerView() {
        Map<String, List<String>> apps = getAllAppsAndGrantedPermissions();
        for (Map.Entry<String, List<String>> entry : apps.entrySet()) {
            list.add(new Model(entry.getKey(), entry.getValue()));
        }
        adapter.notifyDataSetChanged();
    }


    private Map<String, List<String>> getAllAppsAndGrantedPermissions() {
        Map<String, List<String>> appPermissionsMap = new HashMap<>();
        PackageManager packageManager = getPackageManager();

        // Get a list of all installed apps
        List<PackageInfo> installedApps = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (PackageInfo packageInfo : installedApps) {
            // Get app name
            String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            // Check if permissions are requested by the app
            if (packageInfo.requestedPermissions != null) {
                List<String> grantedPermissionsList = new ArrayList<>();
                // Iterate through requested permissions
                for (String permission : packageInfo.requestedPermissions) {
                    try {
                        PermissionInfo permissionInfo = packageManager.getPermissionInfo(permission, 0);
                        // Check if the permission is granted
                        if (permissionInfo != null && packageManager.checkPermission(permission, packageInfo.packageName) == PackageManager.PERMISSION_GRANTED) {
                            grantedPermissionsList.add(permissionInfo.name);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                // Add the app name and its granted permissions to the map
                appPermissionsMap.put(appName, grantedPermissionsList);
            }
        }

        return appPermissionsMap;
    }

    @Override
    public void onItemClickListener(int position, @Nullable Bundle extra) {
        if (extra != null) {
            Model model = extra.getParcelable("Permission");
            if (model != null) {
                Dialog dialog = showDialog(R.layout.display_permission_item, Gravity.CENTER, true);
                TextView txt = dialog.findViewById(R.id.txt_permissions);
                txt.setText("");
                if (model.getPermission() != null) {
                    if (model.getPermission().size() > 0) {
                        List<String> permissions = model.getPermission();
                        for (String str : permissions) {
                            txt.append("* " + str + "\n");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onLongItemClickListener(int position, @Nullable Bundle extra) {

    }

    private boolean checkUnknowSources() {
        int unknownSources = Settings.Secure.getInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 0);
        // if unknownSources = 1 so unknownSources is enabled
        return unknownSources == 1;
    }

    private Dialog showDialog(int dialogLayout, int gravity, boolean isTransparent) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogLayout);
        dialog.show();
        if (isTransparent) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(gravity);

        return dialog;
    }
}