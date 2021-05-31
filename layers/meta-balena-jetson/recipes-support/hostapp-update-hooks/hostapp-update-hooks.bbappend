FILESEXTRAPATHS_append := ":${THISDIR}/files"

DEPENDS_append_jetson-nano    = " tegra210-flash"

HOSTAPP_HOOKS_append_jetson-nano    = " 99-resin-uboot \
					99-resin-bootfiles-nano \
"

DEPENDS_append_jetson-nano-emmc = " tegra210-flash"

HOSTAPP_HOOKS_append_jetson-nano-emmc = " 99-resin-uboot \
                                          99-resin-bootfiles-nano \
"

DEPENDS_append_jetson-nano-2gb-devkit    = " tegra210-flash"

HOSTAPP_HOOKS_append_jetson-nano-2gb-devkit = " 99-resin-uboot \
                                                99-resin-bootfiles-nano \
"

DEPENDS_append_jetson-xavier  = " tegra194-flash-dry"
DEPENDS_append_jetson-xavier-nx-devkit-emmc  = " tegra194-nxde-flash-dry"
DEPENDS_append_jetson-xavier-nx-devkit  = " tegra194-nxde-sdcard-flash"

# Xaviers do not use u-boot, but for rollbacks to work
# we need to update the resinOS_uEnv.txt file
HOSTAPP_HOOKS_append_jetson-xavier  = " \
    99-resin-uboot \
    99-resin-bootfiles-xavier \
"

XAVIER_NX_HOOKS = " \
    99-resin-uboot \
    99-resin-bootfiles-xavier-nx-devkit \
"

HOSTAPP_HOOKS_append_jetson-xavier-nx-devkit-emmc  = " ${XAVIER_NX_HOOKS}"
HOSTAPP_HOOKS_append_jetson-xavier-nx-devkit  = " ${XAVIER_NX_HOOKS}"

DEPENDS_append_tegra186 = " tegra186-flash-dry"

HOSTAPP_HOOKS_append_tegra186 = " \
    99-resin-uboot \
    50-resin-bootfiles-jetson-tx2 \
"
HOSTAPP_HOOKS_append_jetson-tx2-4gb = " \
    99-resin-uboot \
    50-resin-bootfiles-jetson-tx2 \
"

HOSTAPP_HOOKS_append_jetson-tx1 = " \
    99-resin-uboot \
    50-resin-bootfiles-jetson-tx1 \
"
