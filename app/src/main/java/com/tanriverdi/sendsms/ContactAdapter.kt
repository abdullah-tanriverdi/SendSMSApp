
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.tanriverdi.sendsms.R



// ContactAdapter class inherits from ArrayAdapter class and contains data of type String.
class ContactAdapter(context: Context, resource: Int, contacts: List<String>) :
    ArrayAdapter<String>(context, resource, contacts) {

    //// getView function creates custom view for each element.
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        //Custom view is created using LayoutInflater.
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.contact_item, parent, false)

        // The person's name and number are taken.
        val contact = getItem(position)
        val parts = contact?.split(" : ")?.toTypedArray()


        // View elements are defined and filled with data.
        val nameTextView: TextView = view.findViewById(R.id.textViewName)
        val numberTextView: TextView = view.findViewById(R.id.textViewNumber)
        nameTextView.text = parts?.get(0)
        numberTextView.text = parts?.get(1)

        return view
    }
}
