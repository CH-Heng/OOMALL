-- load 库存

-- @author YuJie

local setKey = KEYS[1]
local key = KEYS[2]
local quantity = tonumber(ARGV[1])

if (redis.call('exists', key) == 0) then
    redis.call('sadd', setKey, key)
    redis.call('set', key, quantity)
end
