FROM nginx:stable

ARG APP_EXPOSED_PORT=8080

ENV NGINX_PORT=$APP_EXPOSED_PORT
ENV NGINX_PROXY_PATH=$NGINX_PROXY_PATH
ENV NGINX_PROXY_TARGET_URL=$NGINX_PROXY_TARGET_URL
ENV NGINX_WEBSOCKET_PROXY_TARGET_URL=$NGINX_WEBSOCKET_PROXY_TARGET_URL
ENV NGINX_CSP=$NGINX_CSP
ENV NGINX_SENTRY_REPORT_URI=$NGINX_SENTRY_REPORT_URI

# Configure nginx
RUN mkdir -p /etc/nginx/snippets/
# Copy snippets
COPY .nginx/snippets/ /etc/nginx/snippets/
# Copy configuration files
COPY .nginx/conf.d/ /etc/nginx/conf.d/
# Copy start script
COPY .nginx/start-nginx.sh /usr/sbin/start
# Make start script executable
RUN chmod u+x /usr/sbin/start

# Copy app into image
COPY .dist /app

# App will not run as root, ajust permissions of specific folders.
RUN addgroup nginx root \
  && chmod -R g+rwx /var/cache/nginx /var/run /var/log/nginx /etc/nginx/conf.d /etc/nginx/snippets /tmp /app

# Don't use the port 80 as it may not be allowed by security policy at container runtime.
# If you modify the port, you should impact nginx/conf.d/default.conf.template
EXPOSE $APP_EXPOSED_PORT
WORKDIR /app

USER nginx

CMD [ "start" ]
