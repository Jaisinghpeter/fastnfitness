package com.easyfitness.machines;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.easyfitness.DAO.DAOFonte;
import com.easyfitness.DAO.DAOMachine;
import com.easyfitness.DAO.DAOProfil;
import com.easyfitness.DAO.Machine;
import com.easyfitness.DAO.Profile;
import com.easyfitness.MainActivity;
import com.easyfitness.R;
import com.easyfitness.utils.DateConverter;
import com.easyfitness.utils.Keyboard;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.easyfitness.MainActivity.WEIGHT;

public class MachineFragment extends Fragment {
    final int addId = 555;  //for add exercise menu
    private DAOMachine mDbMachine = null;
    public MapView mMapView;
    Button addButton = null;
    EditText editText = null;
    TextView UsersButton=null;
    private DAOProfil mDbProfils = null;

    private View.OnClickListener userProfleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name=editText.getText().toString();
            ((MainActivity)getActivity()).setCurrentProfil(name);
            ((MainActivity)getActivity()).showFragment(WEIGHT);
            ((MainActivity)getActivity()).setTitle(getResources().getText(R.string.bodytracking));;
            KToast.errorToast(getActivity(), "Current User has been changed "+ name, Gravity.BOTTOM, KToast.LENGTH_SHORT);
        }
    };

    private View.OnClickListener searchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String searchValue=editText.getText().toString();
            if(!searchValue.isEmpty()){

                String[] profilListArray = mDbProfils.getAllProfil();
                int flag=0;
                String name=
                    "";
                for(int i=0;i<profilListArray.length;i++){
                    if(searchValue.equalsIgnoreCase(profilListArray[i])){
                        flag=1;
                        name=profilListArray[i];
                        break;
                    }
                }
                if(flag==1){
                    UsersButton.setVisibility(View.VISIBLE);
                    UsersButton.setText(name);


                    KToast.errorToast(getActivity(), "User Found "+ name, Gravity.BOTTOM, 200);
                }
                else{
                    KToast.errorToast(getActivity(), "User not Found "+name, Gravity.BOTTOM, KToast.LENGTH_SHORT);
                }

//                KToast.errorToast(getActivity(), "Entered Seach Item is "+searchValue, Gravity.BOTTOM, KToast.LENGTH_SHORT);
            }
            else {
                KToast.errorToast(getActivity(), "Search Value is Empty", Gravity.BOTTOM, KToast.LENGTH_SHORT);
            }

        }
    };
    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static MachineFragment newInstance(String name, int id) {
        MachineFragment f = new MachineFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("id", id);
        f.setArguments(args);

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.tag_search, container, false);
        editText = view.findViewById(R.id.textView2);
        mDbProfils = new DAOProfil(getContext());
        UsersButton=view.findViewById(R.id.textView3);
        UsersButton.setVisibility(View.GONE);
        addButton = view.findViewById(R.id.getUserNameIdButton);
        addButton.setOnClickListener(searchListener);
        UsersButton.setOnClickListener(userProfleListener);
        return view;
    }
}
