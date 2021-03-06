package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Paula on 19.2.2018.
 */

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private static final int IS_REGULAR_CRIME = 0;
    private static final int IS_SERIOUS_CRIME = 1;
    private int mLastUpdatedPosition = -1;
    private static final int REQUEST_CRIME = 1;
    private int clickedCrimePosition;
    private static final String CLICKED_CRIME_POSITION_ID = "clicked_crime_position_id";




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view
                .findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

       // updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            if (mLastUpdatedPosition > -1) {
                mAdapter.notifyItemChanged(mLastUpdatedPosition);
                mLastUpdatedPosition = -1;
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Crime mCrime;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;


        public CrimeHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            super(inflater.inflate(viewType, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);

        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
           // mDateTextView.setText(DateFormat.format("EEEE, MMM dd, yyyy", mCrime.getDate()));
            DateFormat dateFormat = new SimpleDateFormat(CrimeFragment.DATE_FORMAT);
            DateFormat timeFormat = new SimpleDateFormat(CrimeFragment.TIME_FORMAT);
            mDateTextView.setText(dateFormat.format(mCrime.getDate()) + " " + timeFormat.format(mCrime.getTime()));
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);

        }

        @Override
        public void onClick(View view) {
            mLastUpdatedPosition = this.getAdapterPosition(); //Challenge: Efficient RecyclerView Reloading
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivityForResult(intent, REQUEST_CRIME);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CRIME) {
            // Handle result
        }
    }

    private class RegularCrimeHolder extends CrimeHolder {
        public RegularCrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater, parent, R.layout.list_item_crime);
        }
    }
    private class SeriousCrimeHolder extends CrimeHolder {
        private Button mContactPoliceButton;

        public SeriousCrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater, parent, R.layout.list_item_crime_police);

            mContactPoliceButton = (Button) itemView.findViewById(R.id.contact_police);
            mContactPoliceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Call the cops!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            if (viewType == IS_SERIOUS_CRIME) {
                return new SeriousCrimeHolder(layoutInflater, parent);
            } else {
                return new RegularCrimeHolder(layoutInflater, parent);
            }
        }
        @Override
        public int getItemViewType(int position) {
            return mCrimes.get(position).isRequiresPolice() ? IS_SERIOUS_CRIME : IS_REGULAR_CRIME;
        }



        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle onSavedInstanceState) {
        super.onSaveInstanceState(onSavedInstanceState);
        onSavedInstanceState.putSerializable(CLICKED_CRIME_POSITION_ID, clickedCrimePosition);
    }
}
