import React, { useState, useEffect } from "react";
import { useDispatch } from "react-redux";

import {
  createFile,
  deleteFile,
  renameFile,
  getFileContent,
  saveFileContent,
} from "../../redux/fileSlice";
import { getDirectoryList } from "../../redux/projectSlice";

import Header from "../../components/Header";

const TEAM_SEQ = 3;
const TYPE_DIRECTORY = 1;
const TYPE_FILE = 2;

const directoryData = {
  rootPath: `/home/ubuntu/crow_data/${TEAM_SEQ}`,
  rootName: ``,
};

const Test = () => {
  const dispatch = useDispatch();
  const [newFileName, setNewFileName] = useState("");
  const [newDirectoryName, setNewDirectoryName] = useState("");
  const [curItems, setCurItems] = useState([]);
  const [curFileContent, setCurFileContent] = useState("");
  const [curFilePath, setCurFilePath] = useState("");

  const dispatchGetDirectoryList = () => {
    dispatch(getDirectoryList(directoryData))
      .unwrap()
      .then((res) => {
        console.log("directoryList res:", res);
        setCurItems(res.fileDirectory);
      })
      .catch(console.error);
  };

  useEffect(() => {
    dispatchGetDirectoryList();
  }, []);

  // 디렉터리 생성 핸들러
  const createDirectoryHandler = (e) => {
    e.preventDefault();
    if (newDirectoryName.trim().length === 0) {
      return;
    }
    const fileData = {
      fileTitle: newDirectoryName,
      filePath: `/home/ubuntu/crow_data/${TEAM_SEQ}`,
    };
    dispatch(createFile({ teamSeq: TEAM_SEQ, type: TYPE_DIRECTORY, fileData }))
      .unwrap()
      .then(() => {
        console.log(`/${newDirectoryName} 생성 완료`);
        setNewDirectoryName("");
        dispatchGetDirectoryList();
      })
      .catch(console.error);
  };

  // 파일 생성 핸들러
  const createFileHandler = (e) => {
    e.preventDefault();
    if (newFileName.trim().length === 0) {
      return;
    }
    const fileData = {
      fileTitle: newFileName,
      filePath: `/home/ubuntu/crow_data/${TEAM_SEQ}`,
    };
    dispatch(createFile({ teamSeq: TEAM_SEQ, type: TYPE_FILE, fileData }))
      .unwrap()
      .then(() => {
        console.log(`${newFileName} 생성 완료`);
        setNewFileName("");
        dispatchGetDirectoryList();
      })
      .catch(console.error);
  };

  // 파일, 폴더 삭제 핸들러
  const deleteItemHandler = (targetPath, targetTypeName, targetName) => {
    if (!window.confirm(`${targetName} 삭제할거임?`)) {
      return;
    }
    const targetType = targetTypeName === "directory" ? "1" : "2";
    const targetData = {
      filePath: targetPath,
    };
    dispatch(
      deleteFile({ teamSeq: TEAM_SEQ, type: targetType, fileData: targetData })
    )
      .unwrap()
      .then((res) => {
        console.log("삭제 성공 res:", res);
        dispatchGetDirectoryList();
      })
      .catch(console.error);
  };

  // 파일, 폴더 이름 수정 핸들러
  const renameItemHandler = (targetPath, targetName) => {
    const newName = prompt("변경할 이름 입력", targetName);
    const renameData = {
      filePath: targetPath,
      oldFileName: targetName,
      fileTitle: newName,
    };
    dispatch(renameFile(renameData))
      .unwrap()
      .then(() => {
        console.log(`${targetName} -> ${newName} 변경 성공`);
        dispatchGetDirectoryList();
      })
      .catch(console.error);
  };

  // 파일 클릭하면 내용 보여주기
  const showFileContentHandler = (targetType, targetPath) => {
    if (targetType === "directory") {
      console.log("디렉터리임");
      return;
    }
    console.log(targetPath);
    const requireData = {
      filePath: targetPath,
    };
    dispatch(getFileContent(requireData))
      .unwrap()
      .then((res) => {
        console.log(res);
        setCurFileContent(res);
        setCurFilePath(targetPath);
      })
      .catch(console.error);
  };

  // 파일 저장
  const fileSaveHandler = (e) => {
    e.preventDefault();
    console.log(curFileContent, curFilePath);
    const saveFileData = {
      filePath: curFilePath,
      fileContent: curFileContent,
    };
    dispatch(saveFileContent({ teamSeq: TEAM_SEQ, contentData: saveFileData }))
      .unwrap()
      .then(console.log)
      .catch(console.error);
  };

  return (
    <React.Fragment>
      <Header />

      {/* 디렉터리 생성 */}
      <form method="post" onSubmit={createDirectoryHandler} className="mb-5">
        <label htmlFor="newDirectory">폴더 생성</label>
        <input
          type="text"
          name="newDirectory"
          id="newDirectory"
          placeholder="생성할 디렉터리 이름 입력"
          value={newDirectoryName}
          onChange={(e) => setNewDirectoryName(e.target.value)}
        />
      </form>

      {/* 파일 생성 */}
      <form method="post" onSubmit={createFileHandler} className="mb-5">
        <label htmlFor="newFile">파일 생성</label>
        <input
          type="text"
          name="newFile"
          id="newFile"
          placeholder="생성할 파일 이름 입력"
          value={newFileName}
          onChange={(e) => setNewFileName(e.target.value)}
        />
      </form>

      {/* 현재 프로젝트 아이템들 */}
      {curItems &&
        curItems?.map((item) => (
          <div className="mb-4 text-sm" key={item.path}>
            <div>경로 {item.path}</div>
            <div
              className="cursor-pointer"
              onClick={() => showFileContentHandler(item.type, item.path)}
            >
              이름 {item.name}
            </div>
            <div>타입 {item.type}</div>
            <div>
              <button
                className="mr-2"
                onClick={() => renameItemHandler(item.path, item.name)}
              >
                ✏
              </button>
              <button
                onClick={() =>
                  deleteItemHandler(item.path, item.type, item.name)
                }
              >
                ❌
              </button>
            </div>
          </div>
        ))}

      {/* 파일 내용 에디터 */}
      <form method="post" onSubmit={fileSaveHandler}>
        <textarea
          rows="10"
          cols="40"
          className="text-black"
          value={curFileContent}
          onChange={(e) => setCurFileContent(e.target.value)}
        ></textarea>
        <button type="submit" onClick={fileSaveHandler}>
          💾
        </button>
      </form>
    </React.Fragment>
  );
};

export default Test;
