package my.project.template.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import my.project.template.R;
import my.project.template.models.NavDrawerBean;

import java.util.ArrayList;


/**
 * @author Devishankar
 */
public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerBean> navDrawerItems;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerBean> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.lv_layout_nav_drawer_list, parent, false);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.counter = (TextView) convertView.findViewById(R.id.counter);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icon.setImageResource(navDrawerItems.get(position).getIcon());
        holder.icon.setColorFilter(context.getResources().getColor(R.color.nav_drawer_menu_color));
        holder.title.setText(navDrawerItems.get(position).getTitle());
        return convertView;
    }

    static class ViewHolder {
        ImageView icon;
        TextView title;
        TextView counter;
    }

}
