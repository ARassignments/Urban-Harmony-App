package com.example.urbanharmony.Screens.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.FaqModel;
import com.example.urbanharmony.Models.StyleModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.DashboardActivity;
import com.example.urbanharmony.Screens.StylesActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FaqFragment extends Fragment {

    View view;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "asc";
    ListView listView;
    LinearLayout loader, notifyBar, notfoundContainer;
    ImageView sortBtn;
    EditText searchInput;
    TextView searchedWord, totalCount;
    ExtendedFloatingActionButton addBtn;
    ArrayList<FaqModel> datalist = new ArrayList<>();

    //    Dialog Elements
    Dialog faqDialog;
    Button cancelBtn, addDataBtn;
    TextInputEditText questionInput, answerInput;
    TextInputLayout questionLayout, answerLayout;
    TextView title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_faq, container, false);

        sharedPreferences = getContext().getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        listView = view.findViewById(R.id.listView);
        notifyBar = view.findViewById(R.id.notifyBar);
        notfoundContainer = view.findViewById(R.id.notfoundContainer);
        loader = view.findViewById(R.id.loader);
        searchedWord = view.findViewById(R.id.searchedWord);
        totalCount = view.findViewById(R.id.totalCount);
        searchInput = view.findViewById(R.id.searchInput);
        addBtn = view.findViewById(R.id.addBtn);
        sortBtn = view.findViewById(R.id.sortBtn);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchedWord.setText(searchInput.getText().toString().trim());
                searchValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if(!DashboardActivity.getRole().equals("admin")){
            addBtn.setVisibility(View.GONE);
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                faqsForm("add","");
            }
        });

        fetchData("");

        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorting();
            }
        });

        return view;
    }

    public void sorting(){
        if(sortingStatus.equals("asc")){
            sortingStatus = "dsc";
            sortBtn.setImageResource(R.drawable.deasscending_order);
        } else if(sortingStatus.equals("dsc")){
            sortingStatus = "asc";
            sortBtn.setImageResource(R.drawable.asscending_order);
        }
        fetchData("");
    }

    public void fetchData(String data){
        MainActivity.db.child("FAQs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(data.equals("")){
                            FaqModel model = new FaqModel(ds.getKey(),
                                    ds.child("question").getValue().toString(),
                                    ds.child("answer").getValue().toString()
                            );
                            datalist.add(model);
                        } else {
                            if(ds.child("question").getValue().toString().trim().toLowerCase().contains(data.toLowerCase().trim())){
                                FaqModel model = new FaqModel(ds.getKey(),
                                        ds.child("question").getValue().toString(),
                                        ds.child("answer").getValue().toString()
                                );
                                datalist.add(model);
                            }
                        }
                    }
                    if(datalist.size() > 0){
                        loader.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        notfoundContainer.setVisibility(View.GONE);
                        if(sortingStatus.equals("dsc")){
                            Collections.reverse(datalist);
                        }
                        MyAdapter adapter = new MyAdapter(getContext(),datalist);
                        listView.setAdapter(adapter);
                    } else {
                        loader.setVisibility(View.GONE);
                        listView.setVisibility(View.GONE);
                        notfoundContainer.setVisibility(View.VISIBLE);
                        if(!data.equals("")){
                            notfoundContainer.setVisibility(View.VISIBLE);
                        }
                    }
                    totalCount.setText(datalist.size()+" found");
                } else {
                    loader.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    notfoundContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void faqsForm(String purpose, String productId){
        faqDialog = new Dialog(getContext());
        faqDialog.setContentView(R.layout.dialog_add_faqs);
        faqDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        faqDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        faqDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        faqDialog.getWindow().setGravity(Gravity.CENTER);
        faqDialog.setCancelable(false);
        faqDialog.setCanceledOnTouchOutside(false);
        cancelBtn = faqDialog.findViewById(R.id.cancelBtn);
        addDataBtn = faqDialog.findViewById(R.id.addDataBtn);
        title = faqDialog.findViewById(R.id.title);
        questionInput = faqDialog.findViewById(R.id.questionInput);
        answerInput = faqDialog.findViewById(R.id.answerInput);
        questionLayout = faqDialog.findViewById(R.id.questionLayout);
        answerLayout = faqDialog.findViewById(R.id.answerLayout);

        questionInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                questionValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        answerInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                answerValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation(purpose, productId);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                faqDialog.dismiss();
            }
        });

        if(purpose.equals("edit")){
            title.setText("Edit FAQ");
            MainActivity.db.child("FAQs").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        questionInput.setText(snapshot.child("question").getValue().toString().trim());
                        answerInput.setText(snapshot.child("answer").getValue().toString().trim());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        faqDialog.show();
    }

    public boolean questionValidation(){
        String input = questionInput.getText().toString().trim();
        String regex = "^[a-zA-Z?,.;'\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            questionLayout.setError("FAQ Question is Required!!!");
            return false;
        } else if(input.length() < 10){
            questionLayout.setError("FAQ Question at least 10 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            questionLayout.setError("Only text allowed!!!");
            return false;
        } else {
            questionLayout.setError(null);
            return true;
        }
    }

    public boolean answerValidation(){
        String input = answerInput.getText().toString().trim();
        String regex = "^[a-zA-Z?,!_'.\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            answerLayout.setError("FAQ Answer is Required!!!");
            return false;
        } else if(input.length() < 10){
            answerLayout.setError("FAQ Answer at least 10 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            answerLayout.setError("Only text allowed!!!");
            return false;
        } else {
            answerLayout.setError(null);
            return true;
        }
    }

    private void validation(String purpose, String productId) {
        boolean questionErr = false, answerErr = false;
        questionErr = questionValidation();
        answerErr = answerValidation();

        if((questionErr && answerErr) == true){
            persons(purpose, productId);
        }
    }

    private void persons(String purpose, String productId){
        if(MainActivity.connectionCheck(getContext())){
            Dialog alertdialog = new Dialog(getContext());
            alertdialog.setContentView(R.layout.dialog_success);
            alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertdialog.getWindow().setGravity(Gravity.CENTER);
            alertdialog.setCancelable(false);
            alertdialog.setCanceledOnTouchOutside(false);
            TextView message = alertdialog.findViewById(R.id.msgDialog);
            alertdialog.show();

            if(purpose.equals("add")){
                HashMap<String, String> mydata = new HashMap<String, String>();
                mydata.put("question", questionInput.getText().toString().trim());
                mydata.put("answer", answerInput.getText().toString().trim());
                MainActivity.db.child("FAQs").push().setValue(mydata);
                message.setText("FAQ Added Successfully!!!");
            } else if(purpose.equals("edit")){
                MainActivity.db.child("FAQs").child(productId).child("question").setValue(questionInput.getText().toString().trim());
                MainActivity.db.child("FAQs").child(productId).child("answer").setValue(answerInput.getText().toString().trim());
                message.setText("FAQ Edited Successfully!!!");
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertdialog.dismiss();
                    faqDialog.dismiss();
                    fetchData("");
                }
            },2000);
        }
    }

    public void searchValidation(){
        String input = searchInput.getText().toString().trim();
        String regex = "^[a-zA-Z\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(!matcher.matches()){
            searchInput.setError("Only text allowed!!!");
        } else {
            searchInput.setError(null);
            if(input.isEmpty()){
                notifyBar.setVisibility(View.GONE);
                fetchData("");
            } else {
                notifyBar.setVisibility(View.VISIBLE);
                fetchData(searchInput.getText().toString().trim());
            }
        }
    }

    class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<FaqModel> data;

        public MyAdapter(Context context, ArrayList<FaqModel> data) {
            this.context = context;
            this.data = data;
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View customListItem = LayoutInflater.from(context).inflate(R.layout.faqs_custom_listview,null);
            TextView question, answer;
            ImageView menu, moreBtn;
            LinearLayout answerContainer;

            question = customListItem.findViewById(R.id.question);
            answer = customListItem.findViewById(R.id.answer);
            menu = customListItem.findViewById(R.id.menu);
            moreBtn = customListItem.findViewById(R.id.moreBtn);
            answerContainer = customListItem.findViewById(R.id.answerContainer);

            question.setText(data.get(i).getQuestion());
            answer.setText(data.get(i).getAnswer());
            if(DashboardActivity.getRole().equals("admin")){
                menu.setVisibility(View.VISIBLE);
            }

            if(data.get(i).isExpanded()){
                answerContainer.setVisibility(View.VISIBLE);
                answerContainer.setScaleY(0f);
                answerContainer.setAlpha(0f);
                answerContainer.animate().scaleY(1f).alpha(1f).setDuration(300).start();
                moreBtn.setRotation(0f);
                moreBtn.animate().rotation(-90f).setDuration(200).start();
            } else {
                answerContainer.setScaleY(1f);
                answerContainer.setAlpha(1f);
                answerContainer.animate().scaleY(0f).alpha(0f).setDuration(300).start();
                moreBtn.setRotation(-90f);
                moreBtn.animate().rotation(0f).setDuration(200).start();
                answerContainer.setVisibility(View.GONE);
            }

            moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.get(i).setExpanded(!data.get(i).isExpanded());
                    notifyDataSetChanged();
                }
            });

            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog loaddialog = new Dialog(context);
                    loaddialog.setContentView(R.layout.dialog_actions);
                    loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    loaddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    loaddialog.getWindow().setGravity(Gravity.CENTER);
                    Button editBtn, deleteBtn;
                    editBtn = loaddialog.findViewById(R.id.editBtn);
                    deleteBtn = loaddialog.findViewById(R.id.deleteBtn);
                    editBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            faqsForm("edit",""+data.get(i).getId());
                            loaddialog.dismiss();
                        }
                    });

                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialog actiondialog = new Dialog(context);
                            actiondialog.setContentView(R.layout.dialog_confirm);
                            actiondialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            actiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            actiondialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                            actiondialog.getWindow().setGravity(Gravity.CENTER);
                            actiondialog.setCancelable(false);
                            actiondialog.setCanceledOnTouchOutside(false);
                            Button cancelBtn, yesBtn;
                            yesBtn = actiondialog.findViewById(R.id.yesBtn);
                            cancelBtn = actiondialog.findViewById(R.id.cancelBtn);
                            cancelBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    actiondialog.dismiss();
                                }
                            });
                            yesBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MainActivity.db.child("FAQs").child(data.get(i).getId()).removeValue();
                                    Dialog dialog = new Dialog(context);
                                    dialog.setContentView(R.layout.dialog_success);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                    dialog.getWindow().setGravity(Gravity.CENTER);
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.setCancelable(false);
                                    TextView msg = dialog.findViewById(R.id.msgDialog);
                                    msg.setText("Deleted Successfully!!!");
                                    dialog.show();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                            actiondialog.dismiss();
                                            loaddialog.dismiss();
                                            fetchData("");
                                        }
                                    },2000);
                                }
                            });

                            actiondialog.show();
                        }
                    });

                    loaddialog.show();
                }
            });

            if(i==data.size()-1){
                customListItem.setPadding(customListItem.getPaddingLeft(), customListItem.getPaddingTop(),customListItem.getPaddingRight(), 30);
            }
            if(i==0){
                customListItem.setPadding(customListItem.getPaddingLeft(), 0,customListItem.getPaddingRight(), 0);
            }
            customListItem.setAlpha(0f);
            customListItem.animate().alpha(1f).setDuration(200).setStartDelay(i * 2).start();
            return customListItem;
        }
    }
}