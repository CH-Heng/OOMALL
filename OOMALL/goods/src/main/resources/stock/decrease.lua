-- 减库存

-- @author YuJie


local setKey = KEYS[1]
local quantity = tonumber(ARGV[1])
math.randomseed(ARGV[2])

if (redis.call('exists', setKey) == 1) then
    local num = redis.call('scard', setKey)
    local keys = redis.call('smembers', setKey)
    local init = math.random(num)

    for i = 1, num do
        local curr = math.fmod(init + i, num) + 1;
        local stock = tonumber(redis.call('get', keys[curr]))
        if (stock >= quantity) then
            local res = redis.call('incrBy', keys[curr], 0 - quantity)
            if (res == 0) then
                redis.call('srem', setKey, keys[curr])
            end
            return res
        end
    end
end

return -1