package dariogonzalez.fitplaygames.Enums;

/**
 * Created by Dario on 8/10/2015.
 */
public enum FriendRequestStatus {
    REQUESTED(0),
    ACCEPTED(1),
    DECLINED(2),
    DELETED(3);
    private int friendRequestStatus;

    FriendRequestStatus(int status)
    {
        friendRequestStatus = status;
    }

    public int GetDirection()
    {
        return friendRequestStatus;
    }
}
