package com.example.gym_manager.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gym_manager.R;
import com.example.gym_manager.adapter.MemberAdapter;
import com.example.gym_manager.databinding.FragmentMemberBinding;
import com.example.gym_manager.listener.FabController;
import com.example.gym_manager.model.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MemberFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

   private FragmentMemberBinding binding;
   private MemberAdapter memberAdapter;
   private List<Member> mList;
    private List<Member> filteredList;
   private FirebaseAuth auth;
    private FabController fabController;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FabController) {
            fabController = (FabController) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FabController");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fabController = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentMemberBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.shimmerLayout.startShimmerAnimation();
        binding.btnBack.setOnClickListener(v->{
            requireActivity().onBackPressed();
        });

        iNit();
        loadMemeberFromFirebase();

        binding.iconSearch.setOnClickListener(v->{
            binding.tvLabelsTitle.setVisibility(View.GONE);
            binding.searchEdt.setVisibility(View.VISIBLE);
            binding.iconSearch.setVisibility(View.GONE);
        });

        binding.searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                //no code
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                filterMembers(s.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void filterMembers(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(mList);
        } else {
            for (Member member : mList) {
                if (member.getName().toLowerCase().contains(query.toLowerCase().trim()) ||
                        member.getPhone().contains(query)) {
                    filteredList.add(member);
                }
            }
        }

        memberAdapter.setMemberList(filteredList);
        memberAdapter.notifyDataSetChanged();

    }

    private void loadMemeberFromFirebase() {
        binding.swiplayout.setRefreshing(true);
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            FirebaseDatabase.getInstance().getReference("members")
                    .child(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            mList.clear();
                            filteredList.clear();
                            if(snapshot.exists()){
                                for(DataSnapshot memberSnapshot : snapshot.getChildren()){
                                    Member member = memberSnapshot.getValue(Member.class);
                                    if(member != null){
                                        mList.add(member);
                                        filteredList.add(member);
                                    }
                                }
                                memberAdapter.notifyDataSetChanged();
                                binding.shimmerLayout.stopShimmerAnimation();
                                binding.shimmerLayout.setVisibility(View.GONE);
                                binding.recyclerview.setVisibility(View.VISIBLE);
                                binding.swiplayout.setRefreshing(false);

                            }
                            else{
                                Toast.makeText(requireContext(), "Chưa có dữ liệu,hãy thêm thành viên", Toast.LENGTH_SHORT).show();
                            }



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            binding.swiplayout.setRefreshing(false);

                        }
                    });
        }
    }

    private void iNit() {
        auth = FirebaseAuth.getInstance();
        mList = new ArrayList<>();
        filteredList = new ArrayList<>();
        memberAdapter = new MemberAdapter(mList);
        binding.recyclerview.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.recyclerview.setAdapter(memberAdapter);
        binding.swiplayout.setOnRefreshListener(this);

        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fabController.collapseFab();
                } else if (dy < 0) {
                    fabController.expandFab();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        loadMemeberFromFirebase();

    }
}