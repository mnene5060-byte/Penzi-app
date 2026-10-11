// Supabase Edge Function: claim-task
// Secure server-side validation for Daily Tasks and Check-ins
// Deploy via: supabase functions deploy claim-task --no-verify-jwt (or with verify-jwt)

import { serve } from "https://deno.land/std@0.177.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2.39.8";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

interface ClaimTaskPayload {
  task_id: string;
  client_timezone?: string;
}

const TASK_REWARDS: Record<string, number | number[]> = {
  "daily_checkin": [10, 10, 10, 15, 20, 25, 30], // Streak day 1 to 7
  "identity_check": 50,
  "first_recharge": 100,
  "profile_bio": 15,
};

serve(async (req: Request) => {
  // Handle CORS Preflight
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const supabaseUrl = Deno.env.get("SUPABASE_URL") ?? "";
    const supabaseServiceKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? "";
    const supabaseAnonKey = Deno.env.get("SUPABASE_ANON_KEY") ?? "";

    const authHeader = req.headers.get("Authorization");
    if (!authHeader) {
      return new Response(
        JSON.stringify({ success: false, message: "Missing Authorization header" }),
        { status: 401, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    // Authenticate user with token
    const client = createClient(supabaseUrl, supabaseAnonKey, {
      global: { headers: { Authorization: authHeader } },
    });

    const { data: { user }, error: authError } = await client.auth.getUser();
    if (authError || !user) {
      return new Response(
        JSON.stringify({ success: false, message: "Invalid or expired token" }),
        { status: 401, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    const userId = user.id;
    const body: ClaimTaskPayload = await req.json();
    const taskId = body.task_id;

    if (!taskId) {
      return new Response(
        JSON.stringify({ success: false, message: "task_id is required" }),
        { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    // Server-side Date calculation based on client's IANA timezone or UTC
    const tz = body.client_timezone || "Africa/Nairobi";
    let todayStr: string;
    try {
      const now = new Date();
      const formatter = new Intl.DateTimeFormat("en-CA", { timeZone: tz, year: "numeric", month: "2-digit", day: "2-digit" });
      todayStr = formatter.format(now); // yyyy-mm-dd
    } catch {
      todayStr = new Date().toISOString().split("T")[0];
    }

    // Use Service Role client to perform atomic updates
    const adminClient = createClient(supabaseUrl, supabaseServiceKey);

    // 1. Check if user already completed this task today
    const { data: existingClaim, error: checkError } = await adminClient
      .from("user_daily_tasks")
      .select("id")
      .eq("user_id", userId)
      .eq("task_id", taskId)
      .eq("claim_date", todayStr)
      .maybeSingle();

    if (existingClaim) {
      return new Response(
        JSON.stringify({
          success: false,
          already_claimed: true,
          task_id: taskId,
          server_date: todayStr,
          message: "Task already collected for today. Resets at midnight.",
        }),
        { status: 200, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    // 2. Calculate Reward & Streak
    let coinsReward = 0;
    let newStreak = 1;

    if (taskId === "daily_checkin") {
      // Get streak record
      const { data: streakRecord } = await adminClient
        .from("user_streaks")
        .select("streak_count, last_checkin_date")
        .eq("user_id", userId)
        .maybeSingle();

      if (streakRecord) {
        const lastDate = new Date(streakRecord.last_checkin_date);
        const todayDate = new Date(todayStr);
        const diffDays = Math.round((todayDate.getTime() - lastDate.getTime()) / (1000 * 3600 * 24));

        if (diffDays === 1) {
          // Consecutive day
          newStreak = ((streakRecord.streak_count % 7) + 1);
        } else if (diffDays === 0) {
          newStreak = streakRecord.streak_count;
        } else {
          newStreak = 1; // Streak reset
        }
      } else {
        newStreak = 1;
      }

      const rewardsArray = TASK_REWARDS["daily_checkin"] as number[];
      coinsReward = rewardsArray[Math.min(newStreak - 1, 6)] || 10;

      // Upsert streak
      await adminClient.from("user_streaks").upsert({
        user_id: userId,
        streak_count: newStreak,
        last_checkin_date: todayStr,
        updated_at: new Date().toISOString(),
      });
    } else {
      coinsReward = (TASK_REWARDS[taskId] as number) || 20;
    }

    // 3. Atomically Insert Task Completion Record
    const { error: insertError } = await adminClient.from("user_daily_tasks").insert({
      user_id: userId,
      task_id: taskId,
      claim_date: todayStr,
      coins_awarded: coinsReward,
    });

    if (insertError) {
      // Catch unique constraint conflict (race condition)
      if (insertError.code === "23505") {
        return new Response(
          JSON.stringify({
            success: false,
            already_claimed: true,
            task_id: taskId,
            server_date: todayStr,
            message: "Task already collected for today.",
          }),
          { status: 200, headers: { ...corsHeaders, "Content-Type": "application/json" } }
        );
      }
      throw insertError;
    }

    return new Response(
      JSON.stringify({
        success: true,
        already_claimed: false,
        task_id: taskId,
        coins_reward: coinsReward,
        new_streak: newStreak,
        server_date: todayStr,
        message: `Claimed +${coinsReward} coins successfully!`,
      }),
      { status: 200, headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  } catch (err: any) {
    return new Response(
      JSON.stringify({ success: false, message: err.message || "Internal server error" }),
      { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  }
});
