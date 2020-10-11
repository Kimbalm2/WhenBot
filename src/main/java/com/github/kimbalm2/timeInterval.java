package com.github.kimbalm2;

public class timeInterval {
   //String start;
   //String end;
   public int start;
   public int end;
   private String s;
   private String e;

   public timeInterval (String start, String end){
      this.start = Integer.parseInt(start.replace(":",""));
      this.end = Integer.parseInt(end.replace(":",""));
      s = start;
      e = end;
   }

   public timeInterval(int start, int end){
      this.start = start;
      this.end = end;

      s = Integer.toString(start);
      e = Integer.toString(start, end);
   }

   public String toString(){
      return s + ":" + e;
   }
   //Now, if arr1[i] has smallest endpoint, it can only intersect with arr2[j]. Similarly, if arr2[j] has smallest endpoint, it can only intersect with arr1[i]. If intersection occurs, find the intersecting segment.
   //[l, r] will be the intersecting segment iff l <= r, where l = max(arr1[i][0], arr2[j][0]) and r = min(arr1[i][1], arr2[j][1]).
   //Increment the i and j pointers accordingly to move ahead.
   public boolean doesIntersect(timeInterval t2){
      int l = Integer.max(start, t2.start);
      int r = Integer.max(end, t2.end);
      return l <= r;
   }

   public timeInterval getIntersection(timeInterval t2){
      int l = Integer.max(start, t2.start);
      int r = Integer.max(end, t2.end);
      return new timeInterval(l,r);
   }


}
