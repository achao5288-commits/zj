-- 检查 user_privacy_profiles 表中是否有记录
SELECT id, user_id, profile_visibility, health_data_visible, contact_info_visible 
FROM user_privacy_profiles 
ORDER BY id;

-- 如果没有记录，执行下面的INSERT语句
-- INSERT INTO `user_privacy_profiles` (`user_id`, `profile_visibility`, `health_data_visible`, `contact_info_visible`, `search_indexed`, `two_factor_auth`, `data_encryption_level`, `auto_logout_minutes`, `share_approval_required`, `default_share_expiry_days`) VALUES
-- (1, 'public', 1, 1, 1, 0, 'basic', 30, 1, 7),
-- (9, 'friends', 0, 0, 0, 1, 'enhanced', 60, 1, 14),
-- (13, 'private', 0, 1, 0, 0, 'basic', 30, 0, 7);
