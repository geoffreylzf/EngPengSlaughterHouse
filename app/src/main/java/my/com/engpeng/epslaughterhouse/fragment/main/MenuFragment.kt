package my.com.engpeng.epslaughterhouse.fragment.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_menu.*
import my.com.engpeng.epslaughterhouse.R

/**
 * A simple [Fragment] subclass.
 *
 */
class MenuFragment : Fragment() {

    private val TAG = MenuFragment::class.qualifiedName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_trip.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_menuFragment_to_tripHeadFragment)
        )

        /*btn_trip.setOnClickListener {
            findNavController()
                    .navigate(R.id.action_menuFragment_to_tripHeadFragment, null,
                            NavOptions.Builder().setPopUpTo(R.id.menuFragment, true).build()
                    )
        }*/

        btn_house_keeping.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_menuFragment_to_houseKeepingFragment)
        )

        btn_test_api.setOnClickListener {

        }
    }
}
