package cn.echo.sentinel.client.adapter;

import com.alibaba.csp.sentinel.*;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

/**
 * okHttp限流扩展逻辑
 * @since  2019-08-17
 */
@Slf4j
public class OkHttpSentinelInterceptor implements Interceptor {


    static final String Prefix = "RPC://";
    //static final String Referer = "Referer";
    static final MediaType JsonMediaType = MediaType.parse("application/json; charset=utf-8");
    public OkHttpSentinelInterceptor() {
        log.info("Initialize okHttp sentinel interceptor.");
    }

    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        HttpUrl uri = request.url();
        //String referer = request.header(Referer);

        String hostWithPathResource = Prefix;
                //+ request.method().toUpperCase() + ":"
                //+ uri.scheme() + "://" + uri.host()
                //+ (uri.port() == -1 ? "" : ":" + uri.port());

        //处理uri.pathSegments()，拼到hostWithPathResource后
        List<String> pathList = uri.pathSegments();
        if(pathList != null && pathList.size() > 0){
            for(String path:pathList){
                hostWithPathResource += path; //"/"+
            }
        }

        Entry hostWithPathEntry = null;
        Response response = null;

        try {
            //对hostWithPathResource限流
            hostWithPathEntry = SphU.entry(hostWithPathResource, ResourceTypeConstants.COMMON_RPC, EntryType.OUT);
            response = chain.proceed(request);

        } catch (BlockException e) {
            //处理限流异常逻辑
            String res = JSON.toJSONString(e.getRule());
            log.error("okHttp request block by sentinel. limitRule:{}", res);

            Response.Builder builder = new Response.Builder();
            builder.code(429); //Too Many Connections
            builder.request(request);
            builder.protocol(Protocol.HTTP_1_1);
            builder.message("Too Many Error Responses");
            builder.body(ResponseBody.create(res, JsonMediaType));
            return builder.build();
        }  catch (IOException ex) {
            //如果业务异常直接抛出
            Tracer.traceEntry(ex, hostWithPathEntry);

            throw ex;
        } finally {
            if (hostWithPathEntry != null) {
                hostWithPathEntry.exit();
            }
        }
        return response;
    }

}
