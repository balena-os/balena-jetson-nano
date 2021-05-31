include balena-image.inc

IMAGE_FSTYPES_append = " hostapp-ext4"

DEVICE_SPECIFIC_SPACE_jetson-nano = "49152"
DEVICE_SPECIFIC_SPACE_jetson-nano-emmc = "49152"
DEVICE_SPECIFIC_SPACE_jetson-nano-2gb-devkit = "49152"

check_size() {
    file_path=${1}
    [ -f "${file_path}" ] || bbfatal "Specified path does not exist: ${file_path}"
    file_size=$(ls -l ${file_path} | awk '{print $5}')
    part_size=${2}

    if [ "$file_size" -ge "$part_size" ]; then
        bbfatal "File ${file_path} too big for raw partition!"
    fi;
}

do_image_balenaos-img_jetson-nano[depends] += " tegra210-flash:do_deploy"
device_specific_configuration_jetson-nano() {
    partitions=$(cat ${DEPLOY_DIR_IMAGE}/tegra-binaries/partition_specification210.txt)
    NVIDIA_PART_OFFSET=2048
    START=${NVIDIA_PART_OFFSET}
    for n in ${partitions}; do
      part_name=$(echo $n | cut -d ':' -f 1)
      file_name=$(echo $n | cut -d ':' -f 2)
      part_size=$(echo $n | cut -d ':' -f 3)
      file_path=$(find ${DEPLOY_DIR_IMAGE}/bootfiles -name $file_name)
      END=$(expr ${START} \+ ${part_size} \- 1)
      parted -s ${BALENA_RAW_IMG} unit s mkpart $part_name ${START} ${END}
      check_size ${file_path} $(expr ${part_size} \* 512)
      dd if=$file_path of=${BALENA_RAW_IMG} conv=notrunc seek=${START} bs=512
      START=$(expr ${START} \+ ${NVIDIA_PART_OFFSET})
    done
}

do_image_balenaos-img_jetson-nano-emmc[depends] += " tegra210-flash:do_deploy"
device_specific_configuration_jetson-nano-emmc() {
    partitions=$(cat ${DEPLOY_DIR_IMAGE}/tegra-binaries/partition_specification210.txt)
    NVIDIA_PART_OFFSET=2048
    START=${NVIDIA_PART_OFFSET}
    for n in ${partitions}; do
      part_name=$(echo $n | cut -d ':' -f 1)
      file_name=$(echo $n | cut -d ':' -f 2)
      part_size=$(echo $n | cut -d ':' -f 3)
      file_path=$(find ${DEPLOY_DIR_IMAGE}/bootfiles -name $file_name)
      END=$(expr ${START} \+ ${part_size} \- 1)
      parted -s ${BALENA_RAW_IMG} unit s mkpart $part_name ${START} ${END}
      check_size ${file_path} $(expr ${part_size} \* 512)
      dd if=$file_path of=${BALENA_RAW_IMG} conv=notrunc seek=${START} bs=512
      START=$(expr ${START} \+ ${NVIDIA_PART_OFFSET})
    done
}

do_image_balenaos-img_jetson-nano-2gb-devkit[depends] += " tegra210-flash:do_deploy"
device_specific_configuration_jetson-nano-2gb-devkit() {
    partitions=$(cat ${DEPLOY_DIR_IMAGE}/tegra-binaries/partition_specification210-2gb.txt)
    NVIDIA_PART_OFFSET=2048
    START=${NVIDIA_PART_OFFSET}
    for n in ${partitions}; do
      part_name=$(echo $n | cut -d ':' -f 1)
      file_name=$(echo $n | cut -d ':' -f 2)
      part_size=$(echo $n | cut -d ':' -f 3)
      file_path=$(find ${DEPLOY_DIR_IMAGE}/bootfiles -name $file_name)
      END=$(expr ${START} \+ ${part_size} \- 1)
      parted -s ${BALENA_RAW_IMG} unit s mkpart $part_name ${START} ${END}
      check_size ${file_path} $(expr ${part_size} \* 512)
      dd if=$file_path of=${BALENA_RAW_IMG} conv=notrunc seek=${START} bs=512
      START=$(expr ${START} \+ ${NVIDIA_PART_OFFSET})
    done
}

# We leave this space way larger than currently
# needed because other larger partitions are
# added from one Jetpack release to another
DEVICE_SPECIFIC_SPACE_jetson-xavier = "458752"

# Binaries are signed and packed into
# a partition and the flaser script
# gets them from there. Can't store them
# raw due to partition alignments which
# trigger checksum mismatches during flash

do_image_balenaos-img_jetson-xavier[depends] += " tegra194-flash-dry:do_deploy"
device_specific_configuration_jetson-xavier() {
    partitions=$(cat ${DEPLOY_DIR_IMAGE}/tegra-binaries/partition_specification194.txt)
    NVIDIA_PART_OFFSET=20480
    START=${NVIDIA_PART_OFFSET}
    for n in ${partitions}; do
      part_name=$(echo $n | cut -d ':' -f 1)
      file_name=$(echo $n | cut -d ':' -f 2)
      part_size=$(echo $n | cut -d ':' -f 3)
      file_path=$(find ${DEPLOY_DIR_IMAGE}/bootfiles -name $file_name)
      END=$(expr ${START} \+ ${part_size} \- 1)
      echo "Will write $part_name from ${START} to ${END} part size: $part_size"
      parted -s ${BALENA_RAW_IMG} unit B mkpart $part_name ${START} ${END}
      # The padding partition exists to allow for the device specific space to
      # be a multiple of 4096. We don't write anything to it for the moment.
      if [ ! "$file_name" = "none.bin" ]; then
        check_size ${file_path} ${part_size}
        dd if=$file_path of=${BALENA_RAW_IMG} conv=notrunc seek=$(expr ${START} \/ 512) bs=512
      fi
      START=$(expr ${END} \+ 1)
    done

}

NVIDIA_PART_OFFSET_tegra186="4097"
DEVICE_SPECIFIC_SPACE_tegra186="49152"

do_image_balenaos-img_tegra186[depends] += " tegra186-flash-dry:do_deploy"

device_specific_configuration_tegra186() {
    partitions=$(cat ${DEPLOY_DIR_IMAGE}/tegra-binaries/partition_specification186.txt)
    start=${NVIDIA_PART_OFFSET}
    for n in ${partitions}; do
        part_name=$(echo $n | cut -d ':' -f 1)
        file_name=$(echo $n | cut -d ':' -f 2)
        part_size=$(echo $n | cut -d ':' -f 3)
        file_path=$(find ${DEPLOY_DIR_IMAGE}/bootfiles -name $file_name)
        end=$(expr ${start} \+ ${part_size} \- 1)
        parted -s ${BALENA_RAW_IMG} unit s mkpart $part_name ${start} ${end}
        check_size ${file_path} $(expr ${part_size} \* 512)
        dd if=$file_path of=${BALENA_RAW_IMG} conv=notrunc seek=${start} bs=512
        start=$(expr ${end} \+ 1)
    done

    idx=1
    while [ $idx -lt 24];
    do
        # parted sets type to Linux Filesystem (type 20 at 0x400) regardless of msftdata
        # flag toggle, therefore use sfdisk to set the partition type. Needs to be
        # done after all partitions are added
        echo 'type=11' | sfdisk -N ${idx} ${BALENA_RAW_IMG}
        idx=$(expr ${idx} \+ 1)
    done

    # another issue with parted is incorrect number of partition entries
    # in gpt header at position 0x250. Fix gpt header so it can be interpreted
    # by the first bootloaders
    sed -e 's/\s*\([\+0-9a-zA-Z]*\).*/\1/' << EOF | fdisk ${BALENA_RAW_IMG}
    x
    l
    28
    r
    w
EOF
}

device_specific_configuration_jetson-tx2-4gb() {
    partitions=$(cat ${DEPLOY_DIR_IMAGE}/tegra-binaries/partition_specification186.txt)
    start=${NVIDIA_PART_OFFSET}
    for n in ${partitions}; do
        part_name=$(echo $n | cut -d ':' -f 1)
        file_name=$(echo $n | cut -d ':' -f 2)
        part_size=$(echo $n | cut -d ':' -f 3)
        file_path=$(find ${DEPLOY_DIR_IMAGE}/bootfiles -name $file_name)
        end=$(expr ${start} \+ ${part_size} \- 1)
        parted -s ${BALENA_RAW_IMG} unit s mkpart $part_name ${start} ${end}
        check_size ${file_path} $(expr ${part_size} \* 512)
        dd if=$file_path of=${BALENA_RAW_IMG} conv=notrunc seek=${start} bs=512
        start=$(expr ${end} \+ 1)
    done

    idx=1
    while [ $idx -lt 24];
    do
        # parted sets type to Linux Filesystem (type 20 at 0x400) regardless of msftdata
        # flag toggle, therefore use sfdisk to set the partition type. Needs to be
        # done after all partitions are added
        echo 'type=11' | sfdisk -N ${idx} ${BALENA_RAW_IMG}
        idx=$(expr ${idx} \+ 1)
    done

    # another issue with parted is incorrect number of partition entries
    # in gpt header at position 0x250. Fix gpt header so it can be interpreted
    # by the first bootloaders
    sed -e 's/\s*\([\+0-9a-zA-Z]*\).*/\1/' << EOF | fdisk ${BALENA_RAW_IMG}
    x
    l
    28
    r
    w
EOF
}

DEVICE_SPECIFIC_SPACE_jetson-tx1 = "40960"
do_image_balenaos-img_jetson-tx1[depends] += " tegra210-flash-dry:do_deploy"
device_specific_configuration_jetson-tx1() {
    partitions=$(cat ${DEPLOY_DIR_IMAGE}/tegra-binaries/partition_specification210_tx1.txt)
    START=34
    for n in ${partitions}; do
      part_name=$(echo $n | cut -d ':' -f 1)
      file_name=$(echo $n | cut -d ':' -f 2)
      part_size=$(echo $n | cut -d ':' -f 3)
      file_path=$(find ${DEPLOY_DIR_IMAGE}/bootfiles -name $file_name)
      END=$(expr ${START} \+ ${part_size} \- 1)
      parted -s ${BALENA_RAW_IMG} unit s mkpart $part_name ${START} ${END}
      check_size ${file_path} $(expr ${part_size} \* 512)
      dd if=$file_path of=${BALENA_RAW_IMG} conv=notrunc seek=${START} bs=512
      START=$(expr ${END} \+ 1)
    done

}

# Binaries are signed and packed into
# a partition and the flaser script
# gets them from there. Can't store them
# raw due to partition alignments which
# trigger checksum mismatches during flash
write_jetson_nx_partitions() {
    partitions=$(cat ${DEPLOY_DIR_IMAGE}/tegra-binaries/${1})
    NVIDIA_PART_OFFSET=20480
    START=${NVIDIA_PART_OFFSET}
    for n in ${partitions}; do
      part_name=$(echo $n | cut -d ':' -f 1)
      file_name=$(echo $n | cut -d ':' -f 2)
      part_size=$(echo $n | cut -d ':' -f 3)
      file_path=$(find ${DEPLOY_DIR_IMAGE}/bootfiles -name $file_name)
      END=$(expr ${START} \+ ${part_size} \- 1)
      echo "Will write $part_name from ${START} to ${END} part size: $part_size"
      parted -s ${BALENA_RAW_IMG} unit B mkpart $part_name ${START} ${END}
      # The padding partition exists to allow for the device specific space to
      # be a multiple of 4096. We don't write anything to it for the moment.
      if [ ! "$file_name" = "none.bin" ]; then
        check_size ${file_path} ${part_size}
        dd if=$file_path of=${BALENA_RAW_IMG} conv=notrunc seek=$(expr ${START} \/ 512) bs=512
      fi
      START=$(expr ${END} \+ 1)
    done
}

# We leave this space way larger than currently
# needed because other larger partitions can be
# added from one Jetpack release to another
DEVICE_SPECIFIC_SPACE_jetson-xavier-nx-devkit-emmc = "458752"
do_image_balenaos-img_jetson-xavier-nx-devkit-emmc[depends] += " tegra194-nxde-flash-dry:do_deploy"
device_specific_configuration_jetson-xavier-nx-devkit-emmc() {
    write_jetson_nx_partitions "partition_specification194_nxde.txt"
}

DEVICE_SPECIFIC_SPACE_jetson-xavier-nx-devkit = "458752"
do_image_balenaos-img_jetson-xavier-nx-devkit[depends] += " tegra194-nxde-sdcard-flash:do_deploy"
device_specific_configuration_jetson-xavier-nx-devkit() {
    write_jetson_nx_partitions "partition_specification194_nxde_sdcard.txt"
}
