package com.example.packwiz;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;

import org.joda.time.DateTimeComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class CallLogUtils
{
    private static final String TAG = "CallLogUtils";
    private static CallLogUtils instance;
    private final Context context;
    private ArrayList<CallLogInfo> mainList = null;
    private ArrayList<CallLogInfo> missedCallList = null;
    private ArrayList<CallLogInfo> outgoingCallList = null;
    private ArrayList<CallLogInfo> incomingCallList = null;
    private ArrayList<CallLogInfo> talkedCallList = null;

    private ArrayList<CallLogInfo> zongCalls = null;
    private ArrayList<CallLogInfo> ufoneCalls = null;
    private ArrayList<CallLogInfo> jazzCalls = null;
    private ArrayList<CallLogInfo> telenorCalls = null;

    private CallLogUtils(Context context)
    {
        this.context = context;
    }

    public static CallLogUtils getInstance(Context context)
    {
        if (instance == null)
            instance = new CallLogUtils(context);
        return instance;
    }

    public void loadData()
    {
        mainList = new ArrayList<>();
        missedCallList = new ArrayList<>();
        outgoingCallList = new ArrayList<>();
        incomingCallList = new ArrayList<>();
        talkedCallList = new ArrayList<>();

        zongCalls = new ArrayList<>();
        jazzCalls = new ArrayList<>();
        ufoneCalls = new ArrayList<>();
        telenorCalls = new ArrayList<>();

        String[] projection = {"_id", CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.TYPE, CallLog.Calls.CACHED_NAME};
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();

        try
        {
            Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);

            if (cursor == null)
            {
                Log.d("CALLLOG", "cursor is null");
                return;
            }

            if (cursor.getCount() == 0)
            {
                Log.d("CALLLOG", "cursor size is 0");
                return;
            }

            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                CallLogInfo callLogInfo = new CallLogInfo();
                callLogInfo.setName(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
                callLogInfo.setNumber(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
                callLogInfo.setCallType(cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE)));
                callLogInfo.setDate(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
                callLogInfo.setDuration(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION)));
//                Log.d("Data: ", callLogInfo.getName() + " --- " + callLogInfo.getNumber() + " --- " + callLogInfo.getCallType() + " --- " + callLogInfo.getDate() + " --- " +
//                        "" + callLogInfo.getDuration() );
                mainList.add(callLogInfo);

                switch (Integer.parseInt(callLogInfo.getCallType()))
                {
                    case CallLog.Calls.OUTGOING_TYPE:
                        outgoingCallList.add(callLogInfo);
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        incomingCallList.add(callLogInfo);
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        missedCallList.add(callLogInfo);
                        break;
                }

                if (callLogInfo.getNumber().startsWith("030"))
                {
                    jazzCalls.add(callLogInfo);
                } else if (callLogInfo.getNumber().startsWith("031"))
                {
                    zongCalls.add(callLogInfo);
                } else if (callLogInfo.getNumber().startsWith("032"))
                {
                    jazzCalls.add(callLogInfo);
                } else if (callLogInfo.getNumber().startsWith("033"))
                {
                    ufoneCalls.add(callLogInfo);
                } else if (callLogInfo.getNumber().startsWith("034"))
                {
                    telenorCalls.add(callLogInfo);
                }

                if (callLogInfo.getNumber().startsWith("+92030"))
                {
                    jazzCalls.add(callLogInfo);
                } else if (callLogInfo.getNumber().startsWith("+92031"))
                {
                    zongCalls.add(callLogInfo);
                } else if (callLogInfo.getNumber().startsWith("+92032"))
                {
                    jazzCalls.add(callLogInfo);
                } else if (callLogInfo.getNumber().startsWith("+92033"))
                {
                    ufoneCalls.add(callLogInfo);
                } else if (callLogInfo.getNumber().startsWith("+92034"))
                {
                    telenorCalls.add(callLogInfo);
                }

                cursor.moveToNext();
            }
            cursor.close();
        } catch (SecurityException ex)
        {
            Toast.makeText(context, "Security Exception: Fetching Call Logs not Allowed.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Merge and Sort given Call Logs of the given Date
     *
     * @param list ArrayList of CallLogInfo to merge
     * @param d    Date of which Call Logs should be selected
     * @return Merged and Sorted List of Call Logs
     */
    public static ArrayList<CallLogInfo> unifyList(ArrayList<CallLogInfo> list, Date d)
    {
        ArrayList<CallLogInfo> result = new ArrayList<>();
        DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
        for (int i = 0; i < list.size(); i++)
        {
            CallLogInfo current = list.get(i);
            Date oldDate = new Date(current.getDate());
            boolean found = false;
            for (int j = 0; j < result.size(); j++)
            {
                CallLogInfo old = result.get(j);
                Log.d("Comparing", "Old: " + old.getNumber() + " Current: " + current.getNumber());
                if (old.getNumber().equalsIgnoreCase(current.getNumber()))
                {
                    if (dateTimeComparator.compare(oldDate, d) == 0)
                    {
                        old.setDuration(old.getDuration() + current.getDuration());
//                        result.remove(j);
//                        result.add(old);
                        CallLogInfo callLogInfo = result.get(j);
//                        Log.d("Data: ", callLogInfo.getName() + " --- " + callLogInfo.getNumber() + " --- " + callLogInfo.getCallType() + " --- " + callLogInfo.getDate() + " --- " +
//                                "" + callLogInfo.getDuration() );
                        found = true;
                        break;
                    }
                }
            }
            if (!found && (dateTimeComparator.compare(oldDate, d) == 0))
            {
                result.add(current);
                CallLogInfo callLogInfo = current;
                //               Log.d("Added New: ", callLogInfo.getName() + " --- " + callLogInfo.getNumber() + " --- " + callLogInfo.getCallType() + " --- " + callLogInfo.getDate() + " --- " +
//                        "" + callLogInfo.getDuration() );
            }
        }
        Collections.sort(result, new CallLogUtils.SortByDuration());
        return result;
    }

    /**
     * Sorting Comparator for CallLogInfo
     */
    public static class SortByDuration implements Comparator<CallLogInfo>
    {

        public int compare(CallLogInfo a, CallLogInfo b)
        {
            return (int) (b.getDuration() - a.getDuration());
        }
    }

    public ArrayList<CallLogInfo> getZongCalls()
    {
//        loadData();
        return zongCalls;
    }

    public ArrayList<CallLogInfo> getJazzCalls()
    {
//        loadData();
        return jazzCalls;
    }


    public ArrayList<CallLogInfo> getUfoneCalls()
    {
//        loadData();
        return ufoneCalls;
    }


    public ArrayList<CallLogInfo> getTelenorCalls()
    {
//        if (telenorCalls == null)
//        loadData();
        return telenorCalls;
    }


    public ArrayList<CallLogInfo> readCallLogs()
    {
        if (mainList == null)
            loadData();
        return mainList;
    }

    public ArrayList<CallLogInfo> getMissedCalls()
    {
//        if (mainList == null)
//            loadData();
        return missedCallList;
    }

    public ArrayList<CallLogInfo> getIncommingCalls()
    {
//        if (mainList == null)
//            loadData();
        return incomingCallList;
    }

    public ArrayList<CallLogInfo> getTalkedCalls()
    {
//        if (mainList == null)
//            loadData();
        talkedCallList.addAll(getIncommingCalls());
        talkedCallList.addAll(getOutgoingCalls());
        return talkedCallList;
    }

    public ArrayList<CallLogInfo> getOutgoingCalls()
    {
//        if (mainList == null)
//            loadData();
        return outgoingCallList;
    }

    public long[] getAllCallState(String number)
    {
        long output[] = new long[2];
        for (CallLogInfo callLogInfo : mainList)
        {
            if (callLogInfo.getNumber().equals(number))
            {
                output[0]++;
                if (Integer.parseInt(callLogInfo.getCallType()) != CallLog.Calls.MISSED_TYPE)
                    output[1] += callLogInfo.getDuration();
            }
        }
        return output;
    }

    public long[] getIncomingCallState(String number)
    {
        long output[] = new long[2];
        for (CallLogInfo callLogInfo : incomingCallList)
        {
            if (callLogInfo.getNumber().equals(number))
            {
                output[0]++;
                output[1] += callLogInfo.getDuration();
            }
        }
        return output;
    }

    public long[] getOutgoingCallState(String number)
    {
        long output[] = new long[2];
        for (CallLogInfo callLogInfo : outgoingCallList)
        {
            if (callLogInfo.getNumber().equals(number))
            {
                output[0]++;
                output[1] += callLogInfo.getDuration();
            }
        }
        return output;
    }

    public int getMissedCallState(String number)
    {
        int output = 0;
        for (CallLogInfo callLogInfo : missedCallList)
        {
            if (callLogInfo.getNumber().equals(number))
            {
                output++;
            }
        }
        return output;
    }
}