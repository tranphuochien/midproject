package com.tphien.midproject1412171.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tphien.midproject1412171.Modal.Restaurant;
import com.tphien.midproject1412171.R;
import com.tphien.midproject1412171.RestaurantAdapter;
import com.tphien.midproject1412171.tool.MyReaderJson;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class HomeFragment extends Fragment {
    private ArrayList<Restaurant> dataBank;
    private ArrayList<Restaurant> bufferData;
    private RestaurantAdapter restaurantAdapter;
    private int postLast;
    private static final int MAX = 100;
    private static final int BUFFER = 10;
    private static Context context;
    private TextView tvCurPos;
    private TextView tvResult;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }
    public HomeFragment(Context context) {
        // Required empty public constructor
        HomeFragment.context = context;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void loadData() {
        /*int []idAvatars = {R.drawable.avatar, R.drawable.avatar2, R.drawable.avatar3};

        for (int i = 0; i < MAX; i++) {
            dataBank.add(new Restaurant("Nhà hàng " + i, "cống quỳnh", idAvatars));
        }*/

        InputStream inputStream = getResources().openRawResource(R.raw.data_restaurants);
        try {
            dataBank = new MyReaderJson().read(inputStream);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateBufferData() {
        //Nothing to load
        if (postLast == (MAX -1))
            return;

        //not enough entries to load
        if ( (MAX - postLast) < BUFFER ) {
            for (int i = postLast; i < MAX; i++) {
                bufferData.add(dataBank.get(i));
            }
            postLast = MAX - 1;
            return;
        }

        //enough entries to load
        for (int i = postLast; i < postLast + BUFFER; i++) {
            bufferData.add(dataBank.get(i));
        }
        postLast += BUFFER;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dataBank = new ArrayList<>();
        bufferData = new ArrayList<>();

        //Load data into data bank
        loadData();

        updateBufferData();
        restaurantAdapter = new RestaurantAdapter(context, bufferData);

        //Process listView
        final ListView listView =(ListView) view.findViewById(R.id.lvRestaurants);
        listView.setAdapter(restaurantAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                Toast.makeText(context, "row click", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1
                        && listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {
                    Toast.makeText(context, "end", Toast.LENGTH_SHORT).show();
                    updateBufferData();
                    restaurantAdapter.notifyDataSetChanged();
                }
            }
        });

        //Process titlle
        tvCurPos = (TextView) view.findViewById(R.id.tvCurPos);
        tvCurPos.setText("Your position: ");
        tvResult = (TextView) view.findViewById(R.id.tvResult);
        tvResult.setText("There are 100 locals near your");
        return view;
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
    public void onDestroy() {
        super.onDestroy();
        Glide.get(context).clearMemory();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
