package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Paula on 19.2.2018.
 */

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private static final int IS_REGULAR_CRIME = 0;
    private static final int IS_SERIOUS_CRIME = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view
                .findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        mAdapter = new CrimeAdapter(crimes);
        mCrimeRecyclerView.setAdapter(mAdapter);
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
            mDateTextView.setText(DateFormat.format("EEEE, MMM dd, yyyy", mCrime.getDate()));
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);

        }

        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(),
                    mCrime.getTitle() + " clicked!", Toast.LENGTH_SHORT)
                    .show();
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
}
