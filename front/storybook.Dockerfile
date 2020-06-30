FROM nginx:stable

# support running as arbitrary user which belogs to the root group
RUN chmod g+rwx /var/cache/nginx /var/run /var/log/nginx

# users are not allowed to listen on priviliged ports
COPY .nginx/nginx-storybook.conf /etc/nginx/conf.d/default.conf

EXPOSE 8080

# comment user directive as master process is run as user in OpenShift anyhow
RUN sed -i.bak 's/^user/#user/' /etc/nginx/nginx.conf \
  && addgroup nginx root \
  # Put your htpasswd here.
  && mkdir /nginx-auth

USER nginx

COPY storybook-static /usr/share/nginx/html
