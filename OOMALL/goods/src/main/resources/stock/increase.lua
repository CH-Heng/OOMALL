-- 加库存

-- @author YuJie

local setKey = KEYS[1]
local key = KEYS[2]
local quantity = tonumber(ARGV[1])

if (quantity == 0) then
    return 0;
end

if (redis.call('exists', key) == 1) then
    redis.call('sadd', setKey, key)
    return redis.call('incrBy', key, quantity)
end

return -1
