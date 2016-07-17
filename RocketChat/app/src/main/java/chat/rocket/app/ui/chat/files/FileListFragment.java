package chat.rocket.app.ui.chat.files;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import chat.rocket.app.R;

/**
 * Created by julio on 26/11/15.
 */
public class FileListFragment extends Fragment {
    private static final int FILE_REQUEST_CODE = 33;
    private FileCallback mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        //TODO: figureout how to deal with any kind of file here
        i.setType("image/*");
        //i.setType("*/*");
        startActivityForResult(i, FILE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String filePath = data.getDataString();
            //TODO: figureout how to deal with any kind of file here
            mCallback.processFile(filePath,"image");
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (FileCallback) context;
    }
}
