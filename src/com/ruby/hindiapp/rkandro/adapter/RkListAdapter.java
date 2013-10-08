package com.ruby.hindiapp.rkandro.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ruby.hindiapp.rkandro.R;
import com.ruby.hindiapp.rkandro.pojo.RkListItem;

public class RkListAdapter extends BaseAdapter {

private Context context;
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
	private List<RkListItem> rkList;
	
	public RkListAdapter(Context context, List<RkListItem> rkList) {
		this.context = context;
		this.rkList = rkList;
	}
	
	public void setInvoiceList(List<RkListItem> rkList) {
		this.rkList = rkList;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return rkList.size();
	}

	public Object getItem(int position) {
		return rkList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup viewGroup) {
		View row;
		if(view == null){
			LayoutInflater inflater = LayoutInflater.from(context);
			row = inflater.inflate(R.layout.activity_rk_list_detial, null);
			
		}else{
			row = view;
		}
		RkListItem detail = rkList.get(position);
		
		TextView Title = (TextView) row.findViewById(R.id.TextTitle);
		Title.setText(detail.getTitle());
				
		
		return row;
	}
	
}
