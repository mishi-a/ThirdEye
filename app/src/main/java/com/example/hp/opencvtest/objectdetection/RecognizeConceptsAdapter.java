package com.example.hp.opencvtest.objectdetection;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hp.opencvtest.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import clarifai2.dto.prediction.Concept;

//import com.example.hp.opencvtest.objectdetection.R;

public class RecognizeConceptsAdapter extends RecyclerView.Adapter<RecognizeConceptsAdapter.Holder> {

    @NonNull private List<Concept> concepts = new ArrayList<>();

    public RecognizeConceptsAdapter setData(@NonNull List<Concept> concepts) {
       // concepts = concepts.subList(0, min(concepts.size(), 1));

        this.concepts = concepts;
        notifyDataSetChanged();
        return this;
    }

    @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_concept, parent, false));
    }

    @Override public void onBindViewHolder(Holder holder, int position) {
        final Concept concept = concepts.get(position);
        String object_name = concept.name();
        assert object_name != null;
        if(object_name.equals("Electronics") ){
            object_name = "Raspberry pi3";
        }
        else if(object_name.equals("Resistor")){
            object_name = "PIR Motion Sensor";
        }
        holder.label.setText(object_name != null ? object_name : concept.id());
        // holder.probability.setText(String.valueOf(concept.value()));
    }

    @Override public int getItemCount() {
        return concepts.size();
    }

    static final class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.label) TextView label;
        @BindView(R.id.probability) TextView probability;

        public Holder(View root) {
            super(root);
            ButterKnife.bind(this, root);
        }
    }
}
