-- Migration to create tables and functions for Daily Task Claims and Coins in Supabase

-- 1. Create table to track daily user task claims (Atomic & Idempotent)
CREATE TABLE IF NOT EXISTS public.user_daily_tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    task_id TEXT NOT NULL,
    claim_date DATE NOT NULL DEFAULT CURRENT_DATE,
    coins_awarded INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT unique_user_task_daily UNIQUE (user_id, task_id, claim_date)
);

-- 2. Create index for fast lookups
CREATE INDEX IF NOT EXISTS idx_user_daily_tasks_user_date ON public.user_daily_tasks(user_id, claim_date);

-- 3. Create table for user streaks
CREATE TABLE IF NOT EXISTS public.user_streaks (
    user_id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    streak_count INT NOT NULL DEFAULT 1,
    last_checkin_date DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 4. Enable Row Level Security (RLS)
ALTER TABLE public.user_daily_tasks ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_streaks ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view their own task history"
    ON public.user_daily_tasks FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can view their own streak"
    ON public.user_streaks FOR SELECT
    USING (auth.uid() = user_id);
