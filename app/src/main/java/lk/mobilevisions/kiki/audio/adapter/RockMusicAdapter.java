package lk.mobilevisions.kiki.audio.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import lk.mobilevisions.kiki.R;

public class RockMusicAdapter extends ExpandableRecyclerAdapter<RockMusicAdapter.PeopleListItem> {
    public static final int TYPE_PERSON = 1001;

    public RockMusicAdapter(Context context) {
        super(context);

        setItems(getSampleItems());
    }

    public static class PeopleListItem extends ExpandableRecyclerAdapter.ListItem {
        public String Text;

        public PeopleListItem(String group) {
            super(TYPE_HEADER);

            Text = group;
        }

        public PeopleListItem(String first, String last) {
            super(TYPE_PERSON);

            Text = first + " " + last;
        }
    }

    public class HeaderViewHolder extends ExpandableRecyclerAdapter.HeaderViewHolder {


        public HeaderViewHolder(View view) {
            super(view, (ImageView) view.findViewById(R.id.imageView9));


        }

        public void bind(int position) {
            super.bind(position);


        }
    }

    public class PersonViewHolder extends ExpandableRecyclerAdapter.ViewHolder {
        TextView name;

        public PersonViewHolder(View view) {
            super(view);


        }

        public void bind(int position) {

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(inflate(R.layout.play_list_header_item, parent));
            case TYPE_PERSON:
            default:
                return new PersonViewHolder(inflate(R.layout.play_list_child_item, parent));
        }
    }

    @Override
    public void onBindViewHolder(ExpandableRecyclerAdapter.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                ((HeaderViewHolder) holder).bind(position);
                break;
            case TYPE_PERSON:
            default:
                ((PersonViewHolder) holder).bind(position);
                break;
        }
    }

    private List<PeopleListItem> getSampleItems() {
        List<PeopleListItem> items = new ArrayList<>();

        items.add(new PeopleListItem("Friends"));
        items.add(new PeopleListItem("Bill", "Smith"));
        items.add(new PeopleListItem("John", "Doe"));
        items.add(new PeopleListItem("Frank", "Hall"));
        items.add(new PeopleListItem("Sue", "West"));
        items.add(new PeopleListItem("Family"));
        items.add(new PeopleListItem("Drew", "Smith"));
        items.add(new PeopleListItem("Chris", "Doe"));
        items.add(new PeopleListItem("Alex", "Hall"));
        items.add(new PeopleListItem("Associates"));
        items.add(new PeopleListItem("John", "Jones"));
        items.add(new PeopleListItem("Ed", "Smith"));
        items.add(new PeopleListItem("Jane", "Hall"));
        items.add(new PeopleListItem("Tim", "Lake"));
        items.add(new PeopleListItem("Colleagues"));
        items.add(new PeopleListItem("Carol", "Jones"));
        items.add(new PeopleListItem("Alex", "Smith"));
        items.add(new PeopleListItem("Kristin", "Hall"));
        items.add(new PeopleListItem("Pete", "Lake"));

        return items;
    }
}