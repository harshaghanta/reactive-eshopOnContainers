FROM mcr.microsoft.com/mssql/server:2019-latest

# 
RUN mkdir -p /usr/dbscripts
WORKDIR /usr/dbscripts

COPY Setup/CatalogDB/scripts/ /usr/dbscripts/
# RUN chmod +x /usr/dbscripts/db-init.sh

# Run Microsoft SQl Server and initialization script (at the same time)
# Note: If you want to start MsSQL only (without initialization script) you can comment bellow line out, CMD entry from base image will be taken

CMD /bin/bash ./entrypoint.sh
