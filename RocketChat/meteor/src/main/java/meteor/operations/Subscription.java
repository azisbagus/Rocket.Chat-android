package meteor.operations;

/**
 * Created by julio on 19/11/15.
 */
public class Subscription {
    private String mSubId;
    private Meteor mMeteor;

    public Subscription(String subId, Meteor meteor) {
        this.mSubId = subId;
        this.mMeteor = meteor;
    }

    public String getSubscriptionId() {
        return mSubId;
    }

    public void unSubscribe() {
        mMeteor.unsubscribe(mSubId);
    }

    public void unSubscribe(UnsubscribeListener listener) {
        mMeteor.unsubscribe(mSubId, listener);
    }
}
