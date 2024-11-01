package com.demo.music.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.music.R;

import com.demo.music.service.MPMTestWidgetTest;

import java.util.ArrayList;


public class MPMEffect_Adapter extends RecyclerView.Adapter<MPMEffect_Adapter.MyViewHolder> {
    Context context;
    ArrayList<String> list;
    ArrayList<Integer> list_effect_code;

    public MPMEffect_Adapter(Context context, ArrayList<String> arrayList, ArrayList<Integer> arrayList2) {
        this.context = context;
        this.list = arrayList;
        this.list_effect_code = arrayList2;
    }

    @Override 
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_effect, viewGroup, false));
    }

    @RequiresApi(api = 26)
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.effect_name.setText(this.list.get(i));
        myViewHolder.effect_name.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                for (int i2 = 0; i2 < 5; i2++) {
                    MPMTestWidgetTest.updateSeekBarsValues(MPMEffect_Adapter.this.list_effect_code.get(i).intValue(), MPMEffect_Adapter.this.context);
                }
                MPMTestWidgetTest.effectID_forREC = i;
                MPMTestWidgetTest.effectNAME_forREC = MPMEffect_Adapter.this.list.get(i);
                MPMTestWidgetTest.equalizerModel.setEffectId(i);
                MPMTestWidgetTest.equalizerModel.setEffectNAME(MPMEffect_Adapter.this.list.get(i));
                MPMEffect_Adapter.this.notifyDataSetChanged();
            }
        });
        if (MPMTestWidgetTest.effectID_forREC == i) {
            myViewHolder.effect_bg.setBackgroundResource(R.drawable.back_effects);
            myViewHolder.effect_name.setTextColor(Color.parseColor("#ffffff"));
            return;
        }
        myViewHolder.effect_bg.setBackgroundResource(R.drawable.back_effects_unselect);
        myViewHolder.effect_name.setTextColor(Color.parseColor("#878787"));
    }

    @Override 
    public int getItemCount() {
        return this.list.size();
    }

    
    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout effect_bg;
        TextView effect_name;

        public MyViewHolder(@NonNull View view) {
            super(view);
            this.effect_name = (TextView) view.findViewById(R.id.effect_name);
            this.effect_bg = (LinearLayout) view.findViewById(R.id.effect_bg);
        }
    }
}
