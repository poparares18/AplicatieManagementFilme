package com.example.aplicatiemanagementfilme.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.aplicatiemanagementfilme.MainActivity;
import com.example.aplicatiemanagementfilme.R;
import com.example.aplicatiemanagementfilme.WatchListDetailsActivity;
import com.example.aplicatiemanagementfilme.asyncTask.Callback;
import com.example.aplicatiemanagementfilme.database.model.WatchList;
import com.example.aplicatiemanagementfilme.database.service.WatchListService;
import com.example.aplicatiemanagementfilme.util.WatchListViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WatchListFragment extends Fragment {

    public static final String WATCH_LIST_INFORMATION_KEY = "WATCH_LIST_INFORMATION_KEY";
    public static final String WATCH_LIST_POSITION_KEY = "WATCH_LIST_POSITION_KEY";
    private static ListView lvWatchList;
    private FloatingActionButton fabAddWatchList;
    private WatchListService watchListService;

    public static List<WatchList> watchListArray = new ArrayList<>();

    public WatchListFragment() {
        // Required empty public constructor
    }

    public static WatchListFragment newInstance() {
        WatchListFragment fragment = new WatchListFragment();
        Bundle args = new Bundle();
        //args.putSerializable("ceva", (Serializable) currentUserAccount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_watch_list, container, false);

        // Initializare componente
        initComponents(view);

        // Adaugare functio onClick pe fab
        fabAddWatchList.setOnClickListener(onClickAddWatchListListener(view));

        return view;
    }


    // Functii
    // Initializare componente
    private void initComponents(View view) {
        // Components
        lvWatchList = view.findViewById(R.id.lv_watchList);
        fabAddWatchList = view.findViewById(R.id.fab_add_watchList);

        // Watch List service
        watchListService = new WatchListService(view.getContext());

        // Adaugare adapter
        addListViewAdapter();

        // Preluare watch list array din db
        getWatchListArrayFromDb();

        // Adaugare eveniment click pe obiectele din lv
        lvWatchList.setOnItemClickListener(onClickonLVitem());

        // Adaugare eveniment on long click pt editare watch list
        lvWatchList.setOnItemLongClickListener(onLongClickEditWLnameListener());

    }

    // On long click edit watch list
    private AdapterView.OnItemLongClickListener onLongClickEditWLnameListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                alertDialogEditWLname(view, position);
                return true;
            }
        };
    }

    // Alert dialog edit watch list name
    private void alertDialogEditWLname(View view, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Edit watch list name:");

        final EditText input = new EditText(view.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", alertDialogOkEditWLname(position, input));

        builder.setNegativeButton("Cancel", alertDialogEditWLnameCancel());

        builder.show();
    }

    // Alert dialog edit watch list name ok
    private DialogInterface.OnClickListener alertDialogOkEditWLname(int position, EditText input) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newWLname = input.getText().toString();

                if (newWLname.replace(" ", "").length() <= 2) {
                    Toast.makeText(getContext(), getString(R.string.toast_WL_name_short),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Long idWl = watchListArray.get(position).getId();
                    watchListService.updateWatchListNameById(newWLname, idWl,
                            callbackEditWLname(newWLname, position));
                }
            }
        };
    }


    // Callback edit watch list name
    private Callback<Integer> callbackEditWLname(String newWLname, int position) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                watchListArray.get(position).setWlName(newWLname);
                notifyInternalAdapter();
            }
        };
    }


    // Alert dialog edit watch list name cancel
    private DialogInterface.OnClickListener alertDialogEditWLnameCancel() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        };
    }


    // onClick pentru adaugare watch list
    private View.OnClickListener onClickAddWatchListListener(View view) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAddWatchList(view);
            }
        };
    }


    // Adaugare adapter
    private void addListViewAdapter() {
        WatchListViewAdapter adapter = new WatchListViewAdapter(getContext(),
                R.layout.lv_row_watch_list_details, watchListArray, getLayoutInflater());
        lvWatchList.setAdapter(adapter);
    }

    // Adaugare eveniment click pe obiectele din lv
    private AdapterView.OnItemClickListener onClickonLVitem() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), WatchListDetailsActivity.class);
                intent.putExtra(WATCH_LIST_INFORMATION_KEY, (Serializable) watchListArray.get(position));
                intent.putExtra(WATCH_LIST_POSITION_KEY, position);
                startActivity(intent);
            }
        };
    }

    // Notificare schimbare adapter
    public static void notifyInternalAdapter() {
        ArrayAdapter adapter = (ArrayAdapter) lvWatchList.getAdapter();
        adapter.notifyDataSetChanged();
    }


    // Alert dialog pt adaugare watch list
    private void alertDialogAddWatchList(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Watch list name:");

        final EditText input = new EditText(view.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", onClickDialogAddNewWL(input));

        builder.setNegativeButton("Cancel", onClickDialogCancelAddNewWL());

        builder.show();
    }

    // on click CANCEL dialog anulare adaugare nou wl
    private DialogInterface.OnClickListener onClickDialogCancelAddNewWL() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        };
    }

    // on click OK dialog adaugare nou wl
    private DialogInterface.OnClickListener onClickDialogAddNewWL(EditText input) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String watchListName = input.getText().toString();
                if (watchListName.replace(" ", "").length() <= 2) {
                    Toast.makeText(getContext(), getString(R.string.toast_WL_name_short),
                            Toast.LENGTH_SHORT).show();
                } else {
                    long userAccountId = MainActivity.currentUserAccount.getId();
                    WatchList watchList = new WatchList(watchListName, userAccountId);
                    watchListService.insert(watchList, callbackAddWLtoDb());
                }
            }
        };
    }

    // Adaugare watchlist in db
    private Callback<WatchList> callbackAddWLtoDb() {
        return new Callback<WatchList>() {
            @Override
            public void runResultOnUiThread(WatchList result) {
                Toast.makeText(getContext(),
                        getString(R.string.toast_create_WL_WLfragment, result.getWlName()),
                        Toast.LENGTH_LONG).show();
                watchListArray.add(result);
                notifyInternalAdapter();
            }
        };
    }


    // Preluare watch list array din db
    private void getWatchListArrayFromDb() {
        watchListService.getWatchListsByUserAccountId(MainActivity.currentUserAccount.getId(),
                callbackGetAllWLfromDB());
    }

    // Extragere toate wl din db
    private Callback<List<WatchList>> callbackGetAllWLfromDB() {
        return new Callback<List<WatchList>>() {
            @Override
            public void runResultOnUiThread(List<WatchList> result) {
                watchListArray.clear();
                watchListArray.addAll(result);
                notifyInternalAdapter();
            }
        };
    }

}