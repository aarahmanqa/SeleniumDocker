FROM kshivaprasad/java

RUN apt-get update

RUN apt-get install -y curl

RUN apt-get install -y p7zip \
    p7zip-full \
    unace \
    zip \
    unzip

#RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
#RUN dpkg -i google-chrome-stable_current_amd64.deb; apt-get -fy install

# GeckoDriver v0.19.1
#RUN wget -q "https://github.com/mozilla/geckodriver/releases/download/v0.19.1/geckodriver-v0.19.1-linux64.tar.gz" -O /tmp/geckodriver.tgz \
#    && tar zxf /tmp/geckodriver.tgz -C /usr/bin/ \
#    && rm /tmp/geckodriver.tgz

# chromeDriver v2.35
#RUN wget -q "https://chromedriver.storage.googleapis.com/2.35/chromedriver_linux64.zip" -O /tmp/chromedriver.zip \
#    && unzip /tmp/chromedriver.zip -d /usr/bin/ \
#    && rm /tmp/chromedriver.zip

# xvfb - X server display
#ADD xvfb-chromium /usr/bin/xvfb-chromium
#RUN ln -s /usr/bin/xvfb-chromium /usr/bin/google-chrome \
#    && chmod 777 /usr/bin/xvfb-chromium

# create symlinks to chromedriver and geckodriver (to the PATH)
#RUN ln -s /usr/bin/geckodriver /usr/bin/chromium-browser \
#    && chmod 777 /usr/bin/geckodriver \
#    && chmod 777 /usr/bin/chromium-browser

# Install Chrome for Selenium
RUN curl http://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_83.0.4103.116-1_amd64.deb -o /chrome.deb
RUN dpkg -i /chrome.deb || apt-get install -yf
RUN rm /chrome.deb

# Install chromedriver for Selenium
RUN curl https://chromedriver.storage.googleapis.com/83.0.4103.39/chromedriver_linux64.zip -o /tmp/chromedriver.zip \
    && unzip /tmp/chromedriver.zip -d /usr/local/bin/ \
    && rm /tmp/chromedriver.zip

RUN chmod +x /usr/local/bin/chromedriver

COPY testng testng
COPY entrypoint.sh entrypoint.sh
COPY target/SeleniumDocker-1.0-SNAPSHOT-fat-tests.jar SeleniumDocker-1.0-SNAPSHOT-fat-tests.jar
ENTRYPOINT ["/entrypoint.sh"]