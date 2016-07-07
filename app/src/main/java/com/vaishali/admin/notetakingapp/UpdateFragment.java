package com.vaishali.admin.notetakingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UpdateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateFragment extends Fragment implements View.OnClickListener {

    EditText titleupdate,contentupdate;
    Button update;
    private View rootview;
    private Notes mCurrentNote = null;
    DatabaseAdapter d;
    String titleu,contentu;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UpdateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateFragment newInstance(String param1, String param2) {
        UpdateFragment fragment = new UpdateFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        d=new DatabaseAdapter(getActivity());
        // Inflate the layout for this fragment
        rootview=inflater.inflate(R.layout.fragment_update, container, false);
        titleupdate= (EditText) rootview.findViewById(R.id.edit_text_title1);
        contentupdate= (EditText) rootview.findViewById(R.id.edit_text_note1);
        update= (Button) rootview.findViewById(R.id.savebutton1);
        SharedPreferences sp1=this.getActivity().getSharedPreferences("MyData1", Context.MODE_PRIVATE);
        titleu= sp1.getString("title","N/A");
        contentu= sp1.getString("content","N/A");

        titleupdate.setText(titleu);
        contentupdate.setText(contentu);
        update.setOnClickListener(this);
        return rootview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        String title =titleupdate.getText().toString();
        String content = contentupdate.getText().toString();
        SharedPreferences sp=this.getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String em1= sp.getString("email","N/A");


        Notes note = new Notes();
        note.setTitle(title);
        note.setContent(content);
        note.setEmail(em1);
        long id = d.insertData(note);


        if (id < 0) {
            Toast.makeText(getActivity(),"Unsuccessful",Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getActivity(),"Updated the note!",Toast.LENGTH_LONG).show();
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
