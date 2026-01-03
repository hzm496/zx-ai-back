-- 参数说明：
-- ARGV[1]: 用户id
-- ARGV[2]: 用户领取集合key (activity:user:id:set:{activityId})
-- ARGV[3]: 活动库存key (activity:stock:{activityId})

local userSetKey = KEYS[1]
local stockKey = KEYS[2]
local userId = ARGV[1]

-- 1. 检查用户是否已经领取过
local exist = redis.call("SISMEMBER", userSetKey, userId)
if tonumber(exist) == 1 then
    return -1  -- 已领取过
end

-- 2. 获取库存
local stock = redis.call("get", stockKey)
-- 4. 库存存在，转换为数字并检查
local stockNum = tonumber(stock)
if stockNum <= 0 then
    return -2  -- 库存不足
end

-- 5. 库存充足，扣减库存并标记用户已领取
redis.call("DECR", stockKey)
redis.call("SADD", userSetKey, userId)

return 1  -- 领取成功
