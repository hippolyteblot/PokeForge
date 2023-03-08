import com.example.pokeforge.com.example.pokeforge.RemoteSelectionActivity

object NearbyManager {
    private var instance: NearbyManager? = null
    private var activity: RemoteSelectionActivity? = null

    fun getInstance(): NearbyManager {
        if (instance == null) {
            instance = NearbyManager
        }
        return instance!!
    }

    fun setActivity(activity: RemoteSelectionActivity) {
        this.activity = activity
    }

    fun advertiseNewSelection(dna: List<Int>) {
        if (activity == null) return
        // Probablement pb avec un seul dna
        activity!!.updateRemoteSelection(dna)
    }

    fun advertiseValidation(dna: List<Int>) {
        if (activity == null) return
        activity!!.setRemoteValidation(true)
        advertiseNewSelection(dna)
    }

    fun advertiseInvalidation() {
        if (activity == null) return
        activity!!.setRemoteValidation(false)
    }

}
