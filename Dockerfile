FROM sdp.registry.abas.sh/abas/test:2017r4n16p27
USER root
RUN echo 'UseDNS no' >> /etc/ssh/sshd_config
