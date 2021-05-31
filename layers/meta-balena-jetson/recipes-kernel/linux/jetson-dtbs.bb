FILESEXTRAPATHS_append := ":${THISDIR}/linux-tegra"

inherit allarch systemd

DESCRIPTION = "Package for deploying custom dtbs to rootfs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
	file://tegra186-tx2-6.dtb \
	file://tegra186-tx2-cti-ASG006-IMX274-6CAM.dtb \
	file://tegra186-tx2-cti-ASG001-USB3.dtb \
	file://tegra186-tx2-aetina-n510-p3489-0888-a00-00-base.dtb \
	file://tegra186-tx2-aetina-n310-p3489-0888-a00-00-base.dtb \
	file://tegra186-tx2-blackboard.dtb \
	file://tegra186-tx2-cti-ASG001-revG+.dtb \
	file://tegra210-p3448-0002-p3449-0000-b00-jn30b.dtb \
	file://tegra210-nano-cti-NGX003.dtb \
	file://tegra210-nano-cti-NGX003-IMX477-2CAM.dtb \
"

RDEPENDS_${PN} = " bash "

do_install[depends] += " virtual/kernel:do_deploy "

S = "${WORKDIR}"

do_install_jetson-tx2() {
	install -d ${D}/boot/
	install -m 0644 ${WORKDIR}/tegra186-tx2-6.dtb ${D}/boot/tegra186-tx2-6.dtb
	install -m 0644 ${WORKDIR}/tegra186-tx2-cti-ASG006-IMX274-6CAM.dtb ${D}/boot/tegra186-tx2-cti-ASG006-IMX274-6CAM.dtb
	install -m 0644 ${WORKDIR}/tegra186-tx2-cti-ASG001-USB3.dtb ${D}/boot/tegra186-tx2-cti-ASG001-USB3.dtb
	install -m 0644 ${WORKDIR}/tegra186-tx2-aetina-n510-p3489-0888-a00-00-base.dtb ${D}/boot/tegra186-tx2-aetina-n510-p3489-0888-a00-00-base.dtb
	install -m 0644 ${WORKDIR}/tegra186-tx2-aetina-n310-p3489-0888-a00-00-base.dtb ${D}/boot/tegra186-tx2-aetina-n310-p3489-0888-a00-00-base.dtb
	install -m 0644 ${WORKDIR}/tegra186-tx2-blackboard.dtb ${D}/boot/tegra186-tx2-blackboard.dtb
	install -m 0644 ${DEPLOY_DIR_IMAGE}/tegra186-quill-p3310-1000-c03-00-base.dtb ${D}/boot/tegra186-quill-p3310-1000-c03-00-base.dtb
	install -m 0644 ${WORKDIR}/tegra186-tx2-cti-ASG001-revG+.dtb ${D}/boot/tegra186-tx2-cti-ASG001-revG+.dtb
}

do_install_jetson-tx2-4gb() {
	install -d ${D}/boot/
	install -m 0644 ${DEPLOY_DIR_IMAGE}/tegra186-quill-p3489-0888-a00-00-base.dtb ${D}/boot/tegra186-quill-p3489-0888-a00-00-base.dtb
}

do_install_jetson-nano() {
	install -d ${D}/boot/
	install -m 0644 ${DEPLOY_DIR_IMAGE}/tegra210-p3448-0000-p3449-0000-a02.dtb ${D}/boot/tegra210-p3448-0000-p3449-0000-a02.dtb
	install -m 0644 ${DEPLOY_DIR_IMAGE}/tegra210-p3448-0000-p3449-0000-b00.dtb ${D}/boot/tegra210-p3448-0000-p3449-0000-b00.dtb
	install -m 0644 ${WORKDIR}/tegra210-p3448-0002-p3449-0000-b00-jn30b.dtb ${D}/boot/tegra210-p3448-0002-p3449-0000-b00-jn30b.dtb
	install -m 0644 ${WORKDIR}/tegra210-nano-cti-NGX003.dtb ${D}/boot/tegra210-nano-cti-NGX003.dtb
}


do_install_jetson-nano-emmc() {
	install -d ${D}/boot/
	install -m 0644 ${DEPLOY_DIR_IMAGE}/tegra210-p3448-0002-p3449-0000-b00.dtb ${D}/boot/tegra210-p3448-0002-p3449-0000-b00.dtb
	install -m 0644 ${DEPLOY_DIR_IMAGE}/tegra210-p3448-0002-p3449-0000-a02.dtb  ${D}/boot/tegra210-p3448-0002-p3449-0000-a02.dtb
	install -m 0644 ${WORKDIR}/tegra210-p3448-0002-p3449-0000-b00-jn30b.dtb ${D}/boot/tegra210-p3448-0002-p3449-0000-b00-jn30b.dtb
	install -m 0644 ${WORKDIR}/tegra210-nano-cti-NGX003.dtb ${D}/boot/tegra210-nano-cti-NGX003.dtb
	install -m 0644 ${WORKDIR}/tegra210-nano-cti-NGX003-IMX477-2CAM.dtb ${D}/boot/tegra210-nano-cti-NGX003-IMX477-2CAM.dtb
}

do_install_jetson-nano-2gb-devkit() {
	install -d ${D}/boot/
	install -m 0644 ${DEPLOY_DIR_IMAGE}/tegra210-p3448-0003-p3542-0000.dtb  ${D}/boot/tegra210-p3448-0003-p3542-0000.dtb
}

FILES_${PN}_jetson-tx2 += " \
	/boot/tegra186-tx2-6.dtb \
	/boot/tegra186-tx2-cti-ASG006-IMX274-6CAM.dtb \
	/boot/tegra186-tx2-cti-ASG001-USB3.dtb \
	/boot/tegra186-tx2-aetina-n510-p3489-0888-a00-00-base.dtb \
	/boot/tegra186-tx2-aetina-n310-p3489-0888-a00-00-base.dtb \
	/boot/tegra186-tx2-blackboard.dtb \
	/boot/tegra186-quill-p3310-1000-c03-00-base.dtb \
	/boot/tegra186-tx2-cti-ASG001-revG+.dtb \
"
FILES_${PN}_jetson-tx2-4gb += " \
	/boot/tegra186-quill-p3489-0888-a00-00-base.dtb \
"

FILES_${PN}_jetson-nano += " \
	/boot/tegra210-p3448-0000-p3449-0000-a02.dtb \
	/boot/tegra210-p3448-0000-p3449-0000-b00.dtb \
	/boot/tegra210-p3448-0002-p3449-0000-b00-jn30b.dtb \
	/boot/tegra210-nano-cti-NGX003.dtb \
"

FILES_${PN}_jetson-nano-emmc += " \
	/boot/tegra210-p3448-0002-p3449-0000-b00.dtb \
	/boot/tegra210-p3448-0002-p3449-0000-a02.dtb \
	/boot/tegra210-p3448-0002-p3449-0000-b00-jn30b.dtb \
	/boot/tegra210-nano-cti-NGX003.dtb \
	/boot/tegra210-nano-cti-NGX003-IMX477-2CAM.dtb \
"

FILES_${PN}_jetson-nano-2gb-devkit += " \
        /boot/tegra210-p3448-0003-p3542-0000.dtb \
"
