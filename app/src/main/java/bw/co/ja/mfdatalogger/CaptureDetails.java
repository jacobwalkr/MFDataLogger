package bw.co.ja.mfdatalogger;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Created by Jacob on 23/01/2017.
 */

public class CaptureDetails implements Parcelable {

    public final String floor;
    public final String quadrant;
    public final String sampleFrequencyString;
    public final String room;
    public final String locationDetail;
    public final String localActivity;
    public final int duration;

    public CaptureDetails(String floor, String quadrant, String room, String locationDetail,
            String localActivity, int duration, String sampleFrequencyString) {
        this.floor = floor;
        this.quadrant = quadrant;
        this.room = room;
        this.locationDetail = locationDetail;
        this.localActivity = localActivity;
        this.duration = duration;
        this.sampleFrequencyString = sampleFrequencyString;
    }

    public CaptureDetails(Parcel parcel) {
        String[] data = new String[6];
        parcel.readStringArray(data);

        this.floor = data[0];
        this.quadrant = data[1];
        this.room = data[2];
        this.locationDetail = data[3];
        this.localActivity = data[4];
        this.sampleFrequencyString = data[5];

        this.duration = parcel.readInt();
    }

    public String getFullRoomName() {
        return String.format(Locale.UK, "%s%s-%s", this.floor, this.quadrant, this.room);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.floor,
                this.quadrant,
                this.room,
                this.locationDetail,
                this.localActivity,
                this.sampleFrequencyString
        });

        dest.writeInt(this.duration);
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public CaptureDetails createFromParcel(Parcel source) {
            return new CaptureDetails(source);
        }

        @Override // why is this a thing
        public CaptureDetails[] newArray(int size) {
            return new CaptureDetails[size];
        }
    };
}
