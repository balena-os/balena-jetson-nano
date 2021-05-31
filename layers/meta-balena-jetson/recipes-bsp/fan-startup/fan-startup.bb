EXTRAPATHS_prepend := "${THISDIR}/${PN}:"

inherit allarch systemd

DESCRIPTION = "Service for starting fan at boot time to prevent overheating"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " file://fan-startup.service "

RDEPENDS_${PN} = " bash systemd"

S = "${WORKDIR}"

do_install () {
    install -d ${D}/${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/fan-startup.service ${D}/${systemd_unitdir}/system/fan-startup.service
}

SYSTEMD_SERVICE_${PN} = "fan-startup.service"

COMPATIBLE_MACHINE = "(jetson-xavier|tegra186|jetson-xavier-nx-devkit)"
