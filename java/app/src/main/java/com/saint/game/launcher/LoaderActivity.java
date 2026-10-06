package com.saint.game.launcher;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.liulishuo.filedownloader.FileDownloader;
import com.saint.game.R;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class LoaderActivity extends AppCompatActivity {
    private final String link = "http://a0902008.xsph.ru/Crmp.zip";
    public final String path_zip = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Crmp.zip");
    ProgressBar progressBar;
    TextView DownloadType;
    TextView DownloadProgressText;
    TextView DownloadSB;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        FileDownloader.setup(this);

        DownloadType = (TextView) findViewById(R.id.brp_launcher_load_progress_titile);
        DownloadProgressText = (TextView) findViewById(R.id.brp_launcher_load_progress_text);
        DownloadSB = (TextView) findViewById(R.id.brp_launcher_load_progress_text_2);
        progressBar = (ProgressBar) findViewById(R.id.brp_launcher_load_progress_bar);
        File file = new File(Environment.getExternalStorageDirectory() + "/Crmp");
        if (file.exists()) {
            file.delete();
        }
        try {
            CreateDownload(link);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CreateDownload(final String string) throws IOException {
        new Thread(new Runnable() {
            FileOutputStream file = new FileOutputStream(LoaderActivity.this.path_zip);

            InputStream fn;
            URLConnection urlConnection = null;

            public void run() {
                try {
                    URLConnection openConnection = new URL(string).openConnection();
                    this.urlConnection = openConnection;
                    openConnection.connect();
                    int length = this.urlConnection.getContentLength();
                    this.fn = this.urlConnection.getInputStream();
                    byte[] data = new byte[4096];
                    int chet = 0;
                    while (true) {
                        int read = this.fn.read(data, 0, 4096);
                        int count = read;
                        if (read == -1) {
                            break;
                        }
                        chet += count;
                        this.file.write(data, 0, count);
                        int finalChet = chet;
                        LoaderActivity.this.runOnUiThread(new Runnable() {
                            public final void run() {
                                Download(finalChet, length);
                            }
                        });
                    }
                    FileOutputStream fileOutputStream = this.file;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    InputStream inputStream = this.fn;
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                } catch (Exception e3) {
                    Log.e("LoaderActivity", "Ошибка " + e3);
                    FileOutputStream fileOutputStream2 = this.file;
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                    }
                    InputStream inputStream2 = this.fn;
                    if (inputStream2 != null) {
                        try {
                            inputStream2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Throwable th) {
                    FileOutputStream fileOutputStream3 = this.file;
                    if (fileOutputStream3 != null) {
                        try {
                            fileOutputStream3.close();
                        } catch (IOException e5) {
                            e5.printStackTrace();
                        }
                    }
                    InputStream inputStream3 = this.fn;
                    if (inputStream3 != null) {
                        try {
                            inputStream3.close();
                        } catch (IOException e6) {
                            e6.printStackTrace();
                        }
                    }
                    throw th;
                }
                LoaderActivity.this.runOnUiThread(new Runnable() {
                    public final void run() {
                        UnZipProgress();
                    }
                });
                UnZip();
            }
        }).start();
    }

    private void UnZipProgress() {
        DownloadType.setText("Распаковка файлов игры...");
        DownloadProgressText.setVisibility(View.INVISIBLE);
        DownloadSB.setVisibility(View.INVISIBLE);

        // -----------------------------------------------
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(100);
    }

    private void Download(int finalChet, int length) {
        DownloadType.setText("Загрузка файлов игры...");
        // --------------------------------------------------------------------------------
        DownloadSB.setText((finalChet / 1048576) + " MB из " + (length / 1048576) + " MB");
        // --------------------------------------------------------------------------------
        StringBuilder sb = new StringBuilder();
        sb.append(((finalChet / 1048576) * 100) / (length / 1048576));
        sb.append("%");
        DownloadProgressText.setText(sb.toString());
        // --------------------------------------------------------------------------------
        progressBar.setProgress(((finalChet / 1048576) * 100) / (length / 1048576));
    }

    public void UnZip() {
        try {
            new ZipFile(this.path_zip).extractAll(String.valueOf(Environment.getExternalStorageDirectory()));
            new File(this.path_zip).delete();
        } catch (ZipException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }
}