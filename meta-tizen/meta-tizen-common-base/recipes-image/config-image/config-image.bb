SUMMARY = "Config image"
DESCRIPTION = "This Config image"
SECTION = "config"
PR = "r1"
LICENSE = "MIT"
LIC_FILES_CHKSUM ??= "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

RDEPENDS_${PN} += "weston"
RDEPENDS_${PN} += "media-server"
RDEPENDS_${PN} += "wrt-widgets"
RDEPENDS_${PN} += "avsystem"
RDEPENDS_${PN} += "download-provider"
RDEPENDS_${PN} += "run-postinsts"

do_install() {
  mkdir -p ${D}${sysconfdir}
  echo "Tizen on Yocto" > ${D}${sysconfdir}/tizen
  
  touch ${D}${sysconfdir}/environment
  chmod 0644 ${D}${sysconfdir}/environment
  
  mkdir -p ${D}${sysconfdir}/profile.d
cat >${D}${sysconfdir}/profile.d/bash_prompt_custom.sh <<'EOF'
    # set a fancy prompt (overwrite the one in /etc/profile)
    default="\[\e[0m\]"
    usercol='\[\e[1;34m\]' # blue
    hostcol='\[\e[1;32m\]' # green
    pathcol='\[\e[1;33m\]' # yellow
    gitcol='\[\e[1;31m\]' # light red
    termcmd=''
    _p="$";

    if [ "`id -u`" -eq 0 ]; then
        usercol='\[\e[1;31m\]'
        _p="#"
    fi

    PS1="${usercol}\u${default}@${hostcol}\h${default}:${pathcol}\w${default}${gitcol}${default}${_p} ${termcmd}"

    alias ll="ls -lZ"
    alias lr="ls -ltrZ"
    alias la="ls -alZ"

EOF
  
}

pkg_preinst_${PN}() {

    # fix TIVI-2291
    file="$D/etc/modprobe.d/blacklist.conf"
    [ ! -w "$file" ] \
    ||  sed -ri "s/(^blacklist i8042.*$)/#fix from base-general.post \1/" "$file"

}

pkg_postinst_${PN} () {
  chsmack -t $D${sysconfdir}
  chsmack -a 'System::Shared' $D${sysconfdir}
  
  mkdir -p $D${localstatedir}/volatile/log
  mkdir -p $D${localstatedir}/volatile/tmp
  
  chsmack -t $D${localstatedir}/volatile/log
  chsmack -a 'System::Log'  $D${localstatedir}/volatile/log

  touch $D${localstatedir}/volatile/log/lastlog
  touch $D${localstatedir}/volatile/log/faillog
  touch $D${localstatedir}/volatile/log/wtmp
  touch $D${localstatedir}/volatile/log/btmp
  
  mkdir -p $D${sysconfdir}/profile.d
  
  if [ "x$D" != "x" ]; then  
    if [ -e "$D${localstatedir}/log" ] ; then
      cp -fra $D${localstatedir}/log $D${localstatedir}/volatile
      rm -fr $D${localstatedir}/log
      ln -s volatile/log  $D${localstatedir}/log
    fi
  fi
 
  [ "x$D" != "x" ] && exit 1
   mkdir -p $D/etc/profile.d
   chsmack -a System $D/var/lib/buxton/*.db
   chown -R buxton:buxton $D/var/lib/buxton
   buxtonctl set-label base vconf User
   vconftool set -t string db/ail/ail_info "0" -f -s User
   vconftool set -t string db/menuscreen/desktop "0" -f -s User
   vconftool set -t string db/menu_widget/language "en_US.utf8" -f -s User
   chown -R alice:users $D/home/alice
   chown -R bob:users $D/home/bob
   chown -R carol:users $D/home/carol
   chown -R guest:users $D/home/guest
   source $D/etc/tizen-platform.conf
   chsmack -a '*' $D/var/lib/buxton || true
   chsmack -a '*' /var/lib/buxton/* || true
   chmod 0777 $D/var/lib/buxton || true
   chmod 0666 $D/var/lib/buxton/* || true
   ail_initdb
   pkg_initdb
   chmod 0600 $D/var/lib/buxton/* || true
   chmod 0700 $D/var/lib/buxton || true
   chsmack -a System $D/var/lib/buxton/* || true
   chsmack -a System $D/var/lib/buxton || true
   chsmack -a User $D/home/*/.applications/*/*
   chsmack -a User $D/home/*/.applications/*/.*
}

FILES_${PN} = "${sysconfdir}/tizen \
               ${sysconfdir}/environment \
               ${sysconfdir}/profile.d/bash_prompt_custom.sh \
               "
