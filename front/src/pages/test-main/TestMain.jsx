import React, { useState, useRef, useEffect } from "react";
import { useDispatch } from "react-redux";
import { useParams } from "react-router-dom";
import Editor from "@monaco-editor/react";
import styled from "styled-components";
import SplitPane from "react-split-pane";

import { getTeam } from "../../redux/teamSlice";

import fileApi from "../../api/fileApi";
import editorApi from "../../api/editorApi";
// import teamApi from "../../api/teamApi";

import Header from "../../components/Header";
import Sidebar from "./components/sidebar/Sidebar";
import Directory from "./components/sidebar/Directory";
import Git from "./components/sidebar/Git";
import Team from "./components/sidebar/Team";
import Api from "./components/sidebar/Api";
import VariableName from "./components/sidebar/VariableName";
import Settings from "./components/sidebar/Settings";
import ConsoleTerminal from "./components/ConsoleTerminal";

const editorOptions = {
  scrollBeyondLastLine: false,
  fontSize: "14px",
  fontFamily: "JetBrains Mono",
  autoIndent: "advanced",
  wrappingIndent: "same",
  automaticLayout: true,
  wordWrap: true,
};

const TestMain = () => {
  const dispatch = useDispatch();
  const { teamSeq } = useParams();
  // const { teamGit } = useSelector((state) => state.team.value);
  const editorRef = useRef(null); // 에디터 내용
  const editorheightRef = useRef(); // 에디터 높이
  const [showComponent, setShowComponent] = useState("Dir");

  const [editorHeight, setEditorHeight] = useState();
  const [consoleHeight, setConsoleHeight] = useState("");

  const [curPath, setCurPath] = useState(""); // 현재 선택한 폴더나 파일의 경로
  const [curName, setCurName] = useState(""); // 현재 선택한 폴더나 파일의 이름

  // 콘솔 높이 초기값 세팅
  useEffect(() => {
    const tempSize2 = editorheightRef.current.pane2.clientHeight;
    setConsoleHeight(tempSize2);
    // console.log("consoleHeight: " + consoleHeight);
  });

  const checkSize = () => {
    // 에디터 높이 변경값 셋
    const tempSize = editorheightRef.current.state.pane1Size;
    setEditorHeight(tempSize);
    // console.log(
    //   "editorheightRef.current.state.pane1Size: ",
    //   editorheightRef.current.state.pane1Size
    // );
    // 콘솔 높이 변경값 셋
    const tempSize2 = editorheightRef.current.pane2.clientHeight;
    setConsoleHeight(tempSize2);
    // console.log("consoleHeight: " + consoleHeight);
  };

  useEffect(() => {
    dispatch(getTeam(teamSeq)).unwrap().then(console.log).catch(console.error);
  }, [dispatch, teamSeq]);

  const showComponentHandler = (componentName) =>
    setShowComponent(componentName);

  // 파일 클릭하면 내용 보여주기
  const showFileContentHandler = (type, path) => {
    if (type !== "directory") {
      const filePathData = { filePath: path };
      fileApi
        .getFileContent(filePathData)
        .then((res) => editorRef.current.getModel().setValue(res.data))
        .catch(console.error);
    }
  };

  const saveFileContentHandler = async () => {
    try {
      // 1. 파일 포맷 요청
      const beforeFormatData = { text: editorRef.current.getValue() };
      const res1 = await editorApi.sendFormatRequest(
        "python",
        beforeFormatData
      );
      // 2. 파일 포맷 결과 받기
      const formatTicketData = { name: res1.data.data };
      const res2 = await editorApi.getFormatResult("python", formatTicketData);
      // 3. 파일 저장
      const saveFileContent = {
        filePath: curPath,
        fileContent: res2.data.data,
      };
      await fileApi.saveFileContent(teamSeq, saveFileContent);
      // 4. 파일 내용 가져오기
      const filePathData = { filePath: curPath };
      const res3 = await fileApi.getFileContent(filePathData);
      editorRef.current.getModel().setValue(res3.data);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <React.Fragment>
      <div className="h-full w-full">
        <Header />
        <div className="flex w-full" style={{ height: "calc(100% - 80px)" }}>
          <div className="flex">
            <Sidebar
              clickIcon={showComponentHandler}
              showComponent={showComponent}
            />
            {showComponent && (
              <SidebarItems>
                {showComponent === "Dir" && (
                  <Directory
                    showFileContent={showFileContentHandler}
                    saveFileContent={saveFileContentHandler}
                    curPath={curPath}
                    setCurPath={setCurPath}
                    curName={curName}
                    setCurName={setCurName}
                  />
                )}
                {showComponent === "Git" && <Git />}
                {showComponent === "Team" && <Team />}
                {showComponent === "Api" && <Api />}
                {showComponent === "Var" && <VariableName />}
                {showComponent === "Set" && <Settings />}
              </SidebarItems>
            )}
          </div>
          <div
            className="flex flex-col ml-[8px] mr-3 h-full"
            style={
              showComponent === ""
                ? { width: "calc(100vw - 105px)" }
                : { width: "calc(100vw - 400px)" }
            }
          >
            <SplitPane
              style={{ position: "static" }}
              split="horizontal"
              minSize={31}
              defaultSize="64%"
              className="vertical Pane1"
              ref={editorheightRef}
              onDragFinished={checkSize}
            >
              <Editor
                style={{
                  overflow: "auto",
                }}
                height={editorHeight}
                theme="vs-dark"
                defaultLanguage="python"
                onMount={(editor) => {
                  editorRef.current = editor;
                }}
                options={editorOptions}
              />
              <ConsoleTerminal
                teamSeq={teamSeq}
                curPath={curPath}
                consoleHeight={consoleHeight}
              />
            </SplitPane>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export default TestMain;

const SidebarItems = styled.div`
  width: 292px;
  min-width: 0px;
  height: 100%;
  margin-left: 3px;
`;
