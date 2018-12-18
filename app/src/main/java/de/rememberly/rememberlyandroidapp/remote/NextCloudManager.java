package de.rememberly.rememberlyandroidapp.remote;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.network.OnDatatransferProgressListener;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.DownloadFileRemoteOperation;
import com.owncloud.android.lib.resources.files.FileUtils;
import com.owncloud.android.lib.resources.files.ReadFolderRemoteOperation;
import com.owncloud.android.lib.resources.files.RemoveFileRemoteOperation;
import com.owncloud.android.lib.resources.files.UploadFileRemoteOperation;

import java.io.File;

import de.rememberly.rememberlyandroidapp.R;


public class NextCloudManager {

    private OwnCloudClient mClient;
    private Handler mHandler;

    public NextCloudManager(Context context) {
        Uri serverUri = Uri.parse("https://nils-kretschmer.de/nextcloud");
        mClient = OwnCloudClientFactory.createOwnCloudClient(serverUri, context, true);
        mClient.setCredentials(
                OwnCloudCredentialsFactory.newBasicCredentials("nils", "Lis!_nW:GBd$.")
        );

    }

    public void startUpload(File fileToUpload, String mimeType) {

        String remotePath = FileUtils.PATH_SEPARATOR + fileToUpload.getName();
        // Get the last modification date of the file from the file system
        Long timeStampLong = fileToUpload.lastModified() / 1000;
        String timeStamp = timeStampLong.toString();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        UploadFileRemoteOperation uploadFileRemoteOperation =
                new UploadFileRemoteOperation(fileToUpload.getAbsolutePath(), remotePath, mimeType, timeStamp);
        //uploadFileRemoteOperation.addDatatransferProgressListener(this);
        RemoteOperationResult result = uploadFileRemoteOperation.execute(mClient);
        Log.i("Upload result: ", "Message: " + result.getCode() + " Http phrase: " + result.getHttpPhrase()
        + result.getLogMessage() + result.getException() + result.getHttpCode() + result.toString());

    }
    /*
    @Override
    public void onTransferProgress(long progressRate, long totalTransferredSoFar, long totalToTransfer, String fileName) {
        final long percentage = (totalToTransfer > 0 ? totalTransferredSoFar * 100 / totalToTransfer : 0);
        final boolean upload = fileName.contains(getString(R.string.upload_folder_path));
        Log.d(LOG_TAG, "progressRate " + percentage);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                TextView progressView = null;
                if (upload) {
                    progressView = findViewById(R.id.upload_progress);
                } else {
                    progressView = findViewById(R.id.download_progress);
                }
                if (progressView != null) {
                    progressView.setText(Long.toString(percentage) + "%");
                }
            }
        });
    }
    @Override
    public void onRemoteOperationFinish(RemoteOperation operation, RemoteOperationResult result) {
        if (!result.isSuccess()) {
            Toast.makeText(this, R.string.todo_operation_finished_in_fail, Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, result.getLogMessage(), result.getException());

        } else if (operation instanceof ReadFolderRemoteOperation) {
            onSuccessfulRefresh((ReadFolderRemoteOperation) operation, result);

        } else if (operation instanceof UploadFileRemoteOperation) {
            onSuccessfulUpload((UploadFileRemoteOperation) operation, result);

        } else if (operation instanceof RemoveFileRemoteOperation) {
            onSuccessfulRemoteDeletion((RemoveFileRemoteOperation) operation, result);

        } else if (operation instanceof DownloadFileRemoteOperation) {
            onSuccessfulDownload((DownloadFileRemoteOperation) operation, result);

        } else {
            Toast.makeText(this, R.string.todo_operation_finished_in_success, Toast.LENGTH_SHORT).show();
        }
    }
    */
}
