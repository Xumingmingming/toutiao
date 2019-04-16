package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    //判断某个用户对某一项元素是否喜欢
    public int getLikeStatus(int userId,int entityType,int entityId){
        String likeKey= RedisKeyUtil.getLikeKey(entityType,entityId);
        if (jedisAdapter.sismember(likeKey,String.valueOf(userId))){
            return 1;
        }
        String dislikeKey= RedisKeyUtil.getDisLikeKey(entityType,entityId);
        return jedisAdapter.sismember(dislikeKey,String.valueOf(userId))?-1:0;

    }
    /**
     * 喜欢点赞操作
     * 返回 喜欢的数量
     */
    public long like(int userId,int entityType,int entityId){
        String likeKey=RedisKeyUtil.getLikeKey(entityType,entityId);
        //把用户加到喜欢的likeKey的KV中,就是说这条咨询有多少人喜欢
        jedisAdapter.sadd(likeKey,String.valueOf(userId));
        //删除不喜欢中的userId
        String disLikeKey=RedisKeyUtil.getDisLikeKey(entityType,entityId);
        jedisAdapter.srem(disLikeKey,String.valueOf(userId));
        //在返回有多少人喜欢
        return jedisAdapter.scard(likeKey);
    }
    /**
     * 不喜欢点赞操作
     * 返回 喜欢的数量
     */
    public long disLike(int userId,int entityType,int entityId){
        String disLikeKey=RedisKeyUtil.getDisLikeKey(entityType,entityId);
        //把用户加到不喜欢的disLikeKey的KV中
        jedisAdapter.sadd(disLikeKey,String.valueOf(userId));
        //删除喜欢中的userId
        String likeKey=RedisKeyUtil.getLikeKey(entityType,entityId);
        jedisAdapter.srem(likeKey,String.valueOf(userId));
        //在返回有多少人喜欢
        return jedisAdapter.scard(likeKey);
    }
}
