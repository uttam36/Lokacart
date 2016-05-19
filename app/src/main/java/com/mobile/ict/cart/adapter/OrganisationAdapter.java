package com.mobile.ict.cart.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobile.ict.cart.Container.Organisations;
import com.mobile.ict.cart.R;

/**
 * Created by vish on 4/4/16.
 */
public class OrganisationAdapter extends RecyclerView.Adapter<OrganisationAdapter.DataObjectHolder>{

    Context context;
    private static AppCompatRadioButton lastChecked = null;

    private static int lastCheckedPos = 0;

    public static int getLastCheckedPos() {
        return lastCheckedPos;
    }

    public OrganisationAdapter(Context context)
    {
        this.context = context;
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder
    {
        TextView tOrganisation;
        AppCompatRadioButton rbOrganisation;
        public DataObjectHolder(View itemView, Context context)
        {
            super(itemView);
            tOrganisation = (TextView) itemView.findViewById(R.id.tOrganisation);
            rbOrganisation = (AppCompatRadioButton) itemView.findViewById(R.id.rbOrganisation);

            //for default check in first item
            /*if(getAdapterPosition() == 0 && ChangeOrganisationFragment.organisationList.get(0).getIsChecked())
            {
                lastChecked = rbOrganisation;
                lastCheckedPos = 0;
            }*/

            rbOrganisation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatRadioButton rb = (AppCompatRadioButton) v;

                    if (rb.isChecked())
                    {
                        if (lastChecked != null)
                        {
                            if(lastCheckedPos == getAdapterPosition())
                            {
                                //nothing
                            }
                            else
                            {
                                lastChecked.setChecked(false);
                                Organisations.organisationList.get(lastCheckedPos).setIsChecked(false);
                            }
                        }
                        lastChecked = rb;
                        lastCheckedPos = getAdapterPosition();
                    }
                    else
                        lastChecked = null;

                    Organisations.organisationList.get(getAdapterPosition()).setIsChecked(rb.isChecked());

                    for(int i = 0; i < Organisations.organisationList.size(); ++i)
                    {
                        System.out.println("-----------------------------");
                        System.out.println(Organisations.organisationList.get(i).getName());
                        System.out.println(Organisations.organisationList.get(i).getIsChecked());
                        System.out.println(Organisations.organisationList.get(i).getOrgabbr());
                    }
                }
            });
        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_organisation, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view, context);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.tOrganisation.setText(Organisations.organisationList.get(position).getName());
        if(Organisations.organisationList.get(position).getIsChecked())
        {
            holder.rbOrganisation.setChecked(true);
            lastChecked = holder.rbOrganisation;
            lastCheckedPos = position;
        }
    }

    @Override
    public int getItemCount() {
        return Organisations.organisationList.size();
    }


}
