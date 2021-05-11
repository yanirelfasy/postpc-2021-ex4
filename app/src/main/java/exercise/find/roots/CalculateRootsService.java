package exercise.find.roots;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CalculateRootsService extends IntentService {


  public CalculateRootsService() {
    super("CalculateRootsService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent == null) return;
    System.out.println("INSIDE SERVICE");
    long timeStartMs = System.currentTimeMillis();
    long numberToCalculateRootsFor = intent.getLongExtra("number_for_service", 0);
    if (numberToCalculateRootsFor <= 0) {
      Log.e("CalculateRootsService", "can't calculate roots for non-positive input" + numberToCalculateRootsFor);
      return;
    }
    long currentTime = System.currentTimeMillis();
    long root1 = numberToCalculateRootsFor;
    long root2 = 1;
    boolean timeout = false;
    Intent broadcastResult = new Intent();
    for(long i = 2; i < Math.sqrt(numberToCalculateRootsFor); i++){
      currentTime = System.currentTimeMillis();
      if(currentTime - timeStartMs >= 20000){
        timeout = true;
        break;
      }
      if(numberToCalculateRootsFor % i == 0){
        root1 = i;
        root2 = numberToCalculateRootsFor / i;
        break;
      }
    }

    if(!timeout){
      System.out.println("NO TIMEOUT");
      System.out.println(root1);
      System.out.println(root2);
      broadcastResult.setAction("found_roots");
      broadcastResult.putExtra("original_number", numberToCalculateRootsFor);
      broadcastResult.putExtra("root1", root1);
      broadcastResult.putExtra("root2", root2);
      sendBroadcast(broadcastResult);
    }
    else{
      System.out.println("TIMEOUT");
      broadcastResult.setAction("stopped_calculations");
      broadcastResult.putExtra("original_number", numberToCalculateRootsFor);
      broadcastResult.putExtra("time_until_give_up_seconds", currentTime / 1000);
      sendBroadcast(broadcastResult);
    }
  }
}